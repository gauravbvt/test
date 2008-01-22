package com.mindalliance.channels.data.test

import com.mindalliance.channels.nk.NetKernelCategory
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context


/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 21, 2008
 * Time: 2:48:26 PM
 */
class BeanAccessorTests {

   Context context

    BeanAccessorTests(Context ctx) {
        context = ctx
    }

    void dataBeanCreate() {
        use(NetKernelCategory) {
            String xml = '''
             <testBean  version='1.0.0' beanClass='com.mindalliance.channels.metamodel.TestBean' createdOn='Mon Jan 21 15:57:46 EST 2008' root='false'>
              <name dataType='java.lang.String'>A great test</name>
              <successful dataType='java.lang.Boolean'>false</successful>
              <score dataType='java.lang.Double'>100.0</score>
              <parent beanRef='com.mindalliance.channels.metamodel.TestBean'></parent>
              <runs itemClass='com.mindalliance.channels.metamodel.TestRunComponent'>
                <item beanClass='com.mindalliance.channels.metamodel.TestRunComponent'>
                  <date dataType='java.util.Date'>Mon Jan 21 15:57:46 EST 2008</date>
                  <tester dataType='java.lang.String'>John Doe</tester>
                </item>
                <item beanClass='com.mindalliance.channels.metamodel.TestRunComponent'>
                  <date dataType='java.util.Date'>Mon Jan 21 15:57:46 EST 2008</date>
                  <tester dataType='java.lang.String'>Jane Q. Public</tester>
                </item>
              </runs>
            </testBean>
            '''
            String id = context.sourceString("active:data_bean", [type: 'new', db: data('test_dbxml'), id: data('1234'), bean: string(xml)])
            context.respond(bool(true))
        }

    }

}