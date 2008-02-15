package com.mindalliance.channels.modeler

import com.ten60.netkernel.urii.IURAspect
import com.mindalliance.channels.c10n.util.IContinuation
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.mindalliance.channels.nk.bean.IPersistentBean

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 14, 2008
* Time: 4:48:15 PM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractScript implements IScript {

    IURAspect doStep(String step, IContinuation followUp, Map args, INKFConvenienceHelper context) {
        return this."$step"(followUp, args, context)
    }

    String xformFor(IPersistentBean bean, String commitURL, String abortURL , INKFConvenienceHelper context) {
        String operator = """
                  <nvp>
                     <xfPrefix>xf</xfPrefix>
                     <beanInstanceUrl>bean/get</beanInstanceUrl>
                     <eventPrefix>ev</eventPrefix>
                     <xsdSchemaPrefix>xsd</xsdSchemaPrefix>
                     <customSchemaPrefix>xft</customSchemaPrefix>
                     <customSchemaUrl>http://mindalliance.com/schemas/xsd/channels</customSchemaUrl>
                     <acceptSubmissionUrl>$commitURL</acceptSubmissionUrl>
                     <cancelSubmissionUrl$abortURL</cancelSubmissionUrl>
                     <formCssClass>beanForm</formCssClass>
                     <internalQueryUriPrefix>ffcpl:/com/mindalliance/channels/test/metamodel/queries</internalQueryUriPrefix>
                 </nvp>
                          """
        String xform = context.sourceString('active:forms_xgen', [bean: persistentBean(bean), operator: string(operator)])
        return xform
    }

    void updateBean(IPersistentBean bean, INKFConvenienceHelper context) {
        use(NetKernelCategory) {
            context.subrequest('active:data_bean', [type: 'sink', db:bean.db, id:bean.id, bean: persistentBean(bean)])
        }

    }

}