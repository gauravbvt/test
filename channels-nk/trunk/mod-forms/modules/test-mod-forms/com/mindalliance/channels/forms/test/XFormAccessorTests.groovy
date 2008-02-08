package com.mindalliance.channels.forms.test

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.data.util.PersistentBeanCategory
import com.mindalliance.channels.nk.bean.IPersistentBean

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Feb 6, 2008
 * Time: 11:08:12 AM
 * To change this template use File | Settings | File Templates.
 */
class XFormAccessorTests {

    static private String DB = 'test_dbxml'

    Context ctx

    XFormAccessorTests(Context context) {
        ctx = context
    }

    void generateXForm() {
        use(NetKernelCategory, PersistentBeanCategory) {
            // Reset memory
            ctx.subrequest("active:data_memory", [type: 'delete', db: data(DB)])
            ctx.subrequest("active:data_memory", [type: 'sink', db: data(DB), beans: 'ffcpl:/fixtures/testBeans.xml'])
            // Get bean
            IPersistentBean bean = ctx.sourcePersistentBean("active:data_bean", [db: data(DB), id: data('Top')])
             // Build XForm
            String operator = """
                     <nvp>
                        <xfPrefix>xf</xfPrefix>
                        <beanInstanceUrl>bean/get</beanInstanceUrl>
                        <eventPrefix>ev</eventPrefix>
                        <xsdSchemaPrefix>xsd</xsdSchemaPrefix>
                        <customSchemaPrefix>xft</customSchemaPrefix>
                        <customSchemaUrl>http://mindalliance.com/schemas/xsd/channels</customSchemaUrl>
                        <acceptSubmissionUrl>bean/put</acceptSubmissionUrl>
                        <cancelSubmissionUrl>bean/abort</cancelSubmissionUrl>
                        <formCssClass>beanForm</formCssClass>
                    </nvp>
                             """
            String xform = ctx.sourceString('active:forms_xgen', [bean:persistentBean(bean), operator:string(operator)])
            ctx.log(xform, 'info')
            assert xform =~ 'model'
            ctx.respond (bool(true))
        }
    }

}