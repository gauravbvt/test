package com.mindalliance.channels.forms.accessors

import com.mindalliance.channels.nk.NetKernelCategory
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.accessors.AbstractAccessor
import com.mindalliance.channels.data.util.PersistentBeanCategory
import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.forms.xform.BeanXForm
import com.mindalliance.channels.forms.xform.BeanXForm

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 24, 2008
* Time: 8:44:54 PM
*/
class XFormAccessor extends AbstractAccessor {

    // Generates an xform to edit a bean
    // bean: a persistent bean       // TODO -  Make this param an alternative and generate correct XForm based on subject of form
    // operator: an NVP with
                    /*
                    <nvp>
                        <beanInstanceUrl>a URL</beanInstanceUrl>
                        <eventPrefix>a prefix</eventPrefix>
                        <xsdSchemaPrefix>a prefix</xsdSchemaPrefix>
                        <customSchemaPrefix>a URL</customSchemaPrefix>
                        <customSchemaUrl>a URL</customSchemaUrl>
                        <acceptSubmissionUrl>a URL</acceptSubmissionUrl>
                        <cancelSubmissionUrl>a URL</cancelSubmissionUrl>
                        <formCssClass>a name</formCssClass>
                    </nvp>
                    */
    // Returns XML for the XForm
    void source(Context context)  {
        use(NetKernelCategory, PersistentBeanCategory) {
            IPersistentBean bean = context.sourcePersistentBean("this:param:bean")
            Map settings = context.sourceNVP("this:param:operator")
            assert settings.schemaUrl
            assert settings.eventPrefix
            assert settings.beanInstanceUrl
            assert settings.xsdSchemaPrefix
            assert settings.acceptSubmissionUrl
            assert settings.cancelSubmissionUrl
            assert settings.formCssClass
            BeanXForm xform = new BeanXForm(bean, settings)       // BeanXForm generation is only alternative for now
            String xml = xform.toXml() // serialize the xform to xml
            context.respond(string(xml))
        }
     }

}