package com.mindalliance.channels.modeler.accessors

import com.mindalliance.channels.nk.accessors.AbstractAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.data.util.PersistentBeanCategory
import groovy.xml.MarkupBuilder
import com.mindalliance.channels.modeler.IScript
import com.mindalliance.channels.modeler.ScriptRegistry
import com.mindalliance.channels.c10n.util.IContinuation
import com.mindalliance.channels.c10n.aspects.IAspectContinuation
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect
import com.mindalliance.channels.c10n.aspects.ContinuationAspect

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 13, 2008
* Time: 2:30:03 PM
* To change this template use File | Settings | File Templates.
*/
class ActionAccessor extends AbstractAccessor {

    public static final String ICON_NVP_URI = 'ffcpl:/etc/iconTable.xml'
    public static final String DEFAULT_SCRIPT_ICON_NAME = "action"

    // Manages action and continuation requests
    // operator: getActions|start|continue|commit|abort
    // other args depend on operator
    // return depends on operator
    void source(Context context) {
        String operator = context.operator
        switch (operator) {
            case 'getActions': getActions(context); break
            case 'start': startAction(context); break
            case 'continue': continueAction(context); break
            case 'abort': abortAction(context); break
            case 'commit': commitAction(context); break
            default: throw new IllegalArgumentException("Invalid operator $operator")
        }
    }
    // Returns scripted actions available on selected persistent bean
    // beanID: a bean's id
    // beanDB: the bean's db name
    void getActions(Context context) {
        use(NetKernelCategory, PersistentBeanCategory) {
            String beanID = context.sourceString("this:param:beanID")
            String beanDB = context.sourceString("this:param:beanDB")
            IPersistentBean bean = retrieveBean(beanDB, beanID, context)
            if (bean) {
                // Build actions as XML
                StringWriter writer = new StringWriter()
                MarkupBuilder builder = new MarkupBuilder(writer)
                builder.actions(beanId: beanID, beanDb: beanDB) {
                    bean.actions.each {action ->
                        builder.action(name: action.name) {
                            script(action.type)
                            label(action.label)
                            hint(action.hint)
                            icon(this.iconForScript(action.type, context))
                        }
                    }
                }
                context.respond(string(writer.toString()))
            }
        }
    }
    // Starts scripted action (that makes transient state changes)
    // script: the name of the script
    // action: the name of the action (that parameterizes the script)
    // beanId: a bean's id
    // beanDB: the bean's db name
    // session: the session id
    // param: posted arguments
    void startAction(Context context) {
        // Get bean
        use(NetKernelCategory, PersistentBeanCategory) {
            String beanID = context.sourceString("this:param:beanID")
            String beanDB = context.sourceString("this:param:beanDB")
            IPersistentBean bean = retrieveBean(beanDB, beanID, context)
            if (bean) {
                String scriptName = context.sourceString("this:param:script")
                String action = context.sourceString("this:param:action")
                Map args = map(context.params)
                // Get script
                IScript script = new ScriptRegistry(context).getScript(scriptName)
                // Create a continuation with state = script, action, bean as aspect
                IContinuation c10n = createContinuation(context)
                c10n.state['scriptName'] = scriptName
                c10n.state['action'] = actionName
                c10n.state['bean'] = new PersistentBeanAspect(bean)
                // Execute start script with continuation and args (modifies continuation state, returns aspect)
                def response = script.start(c10n, args)
                // Update continuation
                updateContinuation(c10n, context)
                // Responds with aspect
                context.respond(response)
            }
        }
    }
    // Continues scripted action (picking up transient state changes)
    // continuation: continuation id
    // step: step name
    // session: the session id
    // param: posted params
    void continueAction(Context context) {
        use(NetKernelCategory) {
            // Get continuation
            String id = context.sourceString("this:param:continuation")
            IContinuation c10n = sourceContinuation(id, context)
            // Get script and action from continuation
            String scriptName = c10n.state['scriptName']
            String action = c10n.state['action']
            assert scriptName
            assert action
            IScript script = new ScriptRegistry(context).getScript(scriptName)
            // Create follow up continuation
            IContinuation followUp = createFollowUpContinuation(c10n, context)
            // Execute step of script with continuation and args
            Map args = map(context.params)
            String step = context.sourceString("this:param:step")
            def response = script.doStep(step, followUp, args)
            // Update continuation
            updateContinuation(followup, context)
            // Respond with output of script
            context.respond(response)
        }

        throw new Exception("Not implemented yet")
    }
    // Terminates scripted action, abandoning transient state changes
    // continuation: continuation id
    // step: step name
    // session: the session id
    // param: posted params
    void abortAction(Context context) {
        // Get continuation
        String id = context.sourceString("this:param:continuation")
        IContinuation c10n = sourceContinuation(id, context)
        // Get script and action from continuation
        String scriptName = c10n.state['scriptName']
        String action = c10n.state['action']
        assert scriptName
        assert action
        IScript script = new ScriptRegistry(context).getScript(scriptName)
         // Execute abort step (abandons transient state)
        def response = script.abort(c10n, args)
        // Destroy continuation and (recursively) destroy parent continuation if this is only child
        deleteContinuation(c10n, context)
        // Respond with output of script
        context.respond(response)
    }
    // Terminates scripted action, committing transient state changes
    // continuation: continuation id
    // session: the session id
    // param: posted params
    void commitAction(Context context) {
        // Get continuation
        String id = context.sourceString("this:param:continuation")
        IContinuation c10n = sourceContinuation(id, context)
        // Get script and action from continuation
        String scriptName = c10n.state['scriptName']
        String action = c10n.state['action']
        assert scriptName
        assert action
        IScript script = new ScriptRegistry(context).getScript(scriptName)
         // Execute commit step (abandons transient state)
        def response = script.commit(c10n, args)
        // Destroy continuation and (recursively) destroy parent continuation if this is only child
        deleteContinuation(c10n, context)
        // Respond with output of script
        context.respond(response)
    }

    private iconForScript(String scriptName, Context context) {
        String icon
        use(NetKernelCategory) {
            Map icons = context.sourceNVP(ICON_NVP_URI)
            icon = icons[scriptName]

        }
        return icon ?: icons[DEFAULT_SCRIPT_ICON_NAME]
    }

    private IPersistentBean retrieveBean(String beanDB, String beanID, Context context) {
        IPersistentBean bean = null
        try {
            bean = context.sourcePersistentBean('active:data_bean', [db: beanDB, id: beanID])
        }
        catch (Exception e) {
            context.log("Persistent bean not found at db=$beanDB id=$beanID", 'warn')
            context.subrequest("active:HTTPResponseCode", [
                    param: string("<HTTPResponseCode><code>404</code></HTTPResponseCode>"),
                    mimeType: "text/html",
                    expired: true
            ])
        }
        return bean // null if failed
    }

    private IContinuation createContinuation(Context context) {
        IContinuation c10n
        use(NetKernelCategory) {
            IAspectContinuation iac = (IAspectContinuation) context.sourceAspect("active:c10n", [type: 'new'], IAspectContinuation.class)
            c10n = iac.getContinuation()
        }
        return c10n
    }

    private IContinuation createFollowUpContinuation(IContinuation previous, Context context) {
        IContinuation c10n
        use(NetKernelCategory) {
            IAspectContinuation iac = (IAspectContinuation) context.sourceAspect("active:c10n", [type: 'new', id: string(previous.id)], IAspectContinuation.class)
            c10n = iac.getContinuation()
        }
        return c10n
    }

    private void updateContinuation(IContinuation c10nm, Context context) {
        context.subrequest("active:c10n", [type: 'sink', session: sessionURI, continuation: new ContinuationAspect(c10n)])
    }

    private IContinuation sourceContinuation(String id, Context context) {
        IContinuation c10n
        use(NetKernelCategory) {
            IAspectContinuation iac = (IAspectContinuation) context.sourceAspect("active:c10n", [session: sessionURI, id: string(id)], IAspectContinuation.class)
            c10n = iac.getContinuation()
        }
        return c10n
    }

    private void deleteContinuation(IContinuation c10n, Context context) {
        context.subrequest("active:c10n", [type: 'delete', session: sessionURI, id:string(c10n.id)])
    }



}