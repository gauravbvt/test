package com.mindalliance.channels.modeler.scripts

import com.mindalliance.channels.modeler.AbstractScript
import com.ten60.netkernel.urii.IURAspect
import com.mindalliance.channels.c10n.util.IContinuation
import com.mindalliance.channels.nk.bean.IPersistentBean
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.data.util.PersistentBeanCategory
import groovy.xml.MarkupBuilder
import com.mindalliance.channels.nk.aspects.IAspectPersistentBean

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 14, 2008
* Time: 8:46:35 PM
* To change this template use File | Settings | File Templates.
*/
class ProcessXForm extends AbstractScript {       // We don't care about the 'action' for now -- just put up an XForm for the bean and deal with commit and abort.

    IURAspect start(IContinuation c10n, Map args, INKFConvenienceHelper context) {
        IURAspect aspect
        use(NetKernelCategory, PersistentBeanCategory) {
            // Get the target bean from the continuation
            IAspectPersistentBean beanAspect = c10n.state['bean']
            assert beanAspect
            IPersistentBean bean = beanAspect.getPersistentBean()
            // Perform start of action
            String action = c10n.state['action']
            bean.doAction(action, 'start', c10.state) // modifies the continuation state by setting c10.state['editedBean'] to an IAspectPersistentBean
            // Produce an XForm
            String commitURL = "action/commit/${c10n.id}"
            String abortURL = "action/abort/${c10n.id}"
            beanAspect = c10.state['editedBean']
            IPersistentBean editedBean = beanAspect.getPersistentBean()
            String xform = xformFor(editedBean, commitURL, abortURL, context)
            // Get the target div from the args
            String targetDiv = args['target']
            assert targetDiv
            // Put in continuation (will be copied into follow up continuation)
            c10n.state['target'] = targetDiv
            // Build Taconite command to show the XForm
            StringWriter writer = new StringWriter()
            MarkupBuilder builder = new MarkupBuilder(writer)
            builder.taconite() {
               replace(select:"#$targetDiv", xform)
            }
            aspect = string(writer.toString())
        }
        return aspect
    }

    IURAspect abort(IContinuation c10n, Map args, INKFConvenienceHelper context) {
       IURAspect aspect
        use(NetKernelCategory) {
            // Perform abort of action
            IAspectPersistentBean beanAspect = c10n.state['bean']
            assert beanAspect
            IPersistentBean bean = beanAspect.getPersistentBean()
            String action = c10n.state['action']
            bean.doAction(action, 'abort', c10.state) // modifies the continuation state by setting c10.state['editedBean'] to an IAspectPersistentBean
            // Build Taconite command to give feedback
            String targetDiv = c10n.state['target']
            assert targetDiv
            StringWriter writer = new StringWriter()
            MarkupBuilder builder = new MarkupBuilder(writer)
            builder.taconite() {
                replace(select: "#$targetDiv") {
                    div('class': 'feedback', 'Edition cancelled. Nothing was changed') // We'll do something nicer
                }
            }
            aspect = string(writer.toString())
        }
        return aspect
    }

    IURAspect commit(IContinuation c10n, Map args, INKFConvenienceHelper context) {
        IURAspect aspect
         use(NetKernelCategory) {
             // Get the target bean from the continuation
             IAspectPersistentBean beanAspect = c10n.state['bean']
             assert beanAspect
             IPersistentBean bean = beanAspect.getPersistentBean()
             // Get edited bean posted by XForm
             IPersistentBean editedBean = context.sourcePersistentBean("this:param:param")
             assert editedBean
             c10n.state['editedBean'] = editedBean
             // Do commit step of action on target bean
             String action = c10n.state['action']
             bean.doAction(action, 'commit', c10.state)
             // Update target bean (presumed changed by action's commit) and edited bean
             updateBean(bean, context)
             updateBean(editedBean, context)
             // Build Taconite command to give feedback
             String targetDiv = c10n.state['target']
             assert targetDiv
             StringWriter writer = new StringWriter()
             MarkupBuilder builder = new MarkupBuilder(writer)
             builder.taconite() {
                 replace(select: "#$targetDiv") {
                     div('class': 'feedback', 'All changes were successfully submitted.') // We'll do something nicer
                 }
             }
             aspect = string(writer.toString())
         }
         return aspect
    }

}