package com.mindalliance.channels.modeler.scripts

import com.mindalliance.channels.modeler.AbstractScript
import com.ten60.netkernel.urii.IURAspect
import com.mindalliance.channels.c10n.util.IContinuation
import com.mindalliance.channels.nk.bean.IPersistentBean
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.data.util.PersistentBeanCategory
import groovy.xml.MarkupBuilder

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 14, 2008
* Time: 8:46:35 PM
* To change this template use File | Settings | File Templates.
*/
class processXForm extends AbstractScript {       // We don't care about the 'action' for now -- just put up and XForm for the bean and deal with commit and abort.

    IURAspect start(IContinuation c10n, Map args, INKFConvenienceHelper context) {
        IURAspect aspect
        use(NetKernelCategory, PersistentBeanCategory) {
            // Get the bean from the continuation
            String xml = c10n.state['bean']
            assert xml
            IPersistentBean bean = context.toPersistentBean(xml)
            // Produce an XForm
            String commitURL = "/commit/${c10n.id}"
            String abortURL = "/abort/${c10n.id}"
            String xform = xformFor(bean, commitURL, abortURL, context)
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
             // Persist the bean
             String xml = c10n.state['bean']
             assert xml
             IPersistentBean bean = context.toPersistentBean(xml)
             updateBean(bean, context)
             // Build Taconite command to give feedback
             String targetDiv = c10n.state['target']
             assert targetDiv
             StringWriter writer = new StringWriter()
             MarkupBuilder builder = new MarkupBuilder(writer)
             builder.taconite() {
                 replace(select: "#$targetDiv") {
                     div('class': 'feedback', 'All changes were sucessfully submitted.') // We'll do something nicer
                 }
             }
             aspect = string(writer.toString())
         }
         return aspect
    }

}