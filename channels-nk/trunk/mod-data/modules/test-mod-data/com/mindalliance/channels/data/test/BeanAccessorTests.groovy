package com.mindalliance.channels.data.test

import com.mindalliance.channels.nk.NetKernelCategory
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.data.util.PersistentBeanCategory


/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 21, 2008
 * Time: 2:48:26 PM
 */
class BeanAccessorTests {

    static private final String DB = 'test_dbxml'
    Context context

    BeanAccessorTests(Context ctx) {
        context = ctx
    }

    void dataBeanCreate() {
        use(NetKernelCategory) {
            String xml = context.sourceString('ffcpl:/fixtures/testbean1.xml')
            String id = context.sourceString("active:data_bean", [type: 'new', db: data(DB), id: data('1234'), bean: string(xml)])
            assert id == '1234'
            context.respond(bool(true))
            // Leave bean in memory
        }
    }

    void dataBeanGetUpdateDelete() {
        use(NetKernelCategory, PersistentBeanCategory) {
            String xml = context.sourceString('ffcpl:/fixtures/testbean1.xml')
            String id = context.sourceString("active:data_bean", [type: 'new', db: data(DB), id: data('1234'), bean: string(xml)])

            IPersistentBean bean = context.sourcePersistentBean("active:data_bean", [db: data(DB), id: data(id)])
            assert bean.name.value == 'A test'
            bean.name.value = 'A great test'
            context.subrequest("active:data_bean", [type: 'sink', db: data(DB), id: data('1234'), bean: persistentBean(bean)])
            bean = context.sourcePersistentBean("active:data_bean", [db: data(DB), id: data('1234')])
            assert bean.name.value == 'A great test'

            context.subrequest("active:data_bean", [type: 'delete', db: data(DB), id: data('1234')])
            boolean exists = context.isTrue("active:data_bean", [type: 'exists', db: data(DB), id: data('1234')])
            assert !exists
            context.respond(bool(true))
        }
    }

   void dataBeanCalculate() {
       use(NetKernelCategory, PersistentBeanCategory) {
          context.sourceString("active:data_memory", [type: 'sink', db: data(DB), beans: 'ffcpl:/fixtures/testBeans.xml'])
          IPersistentBean bean = context.sourcePersistentBean("active:data_bean", [db: data(DB), id: data('Top')])
           // score
           def score = bean.score.value
           assert score > 0.0
          // successful tests
          def successfulTests = bean.successfulTests.list
          assert successfulTests.size() == 2
          // most expensive subtest
          IPersistentBean costliest = bean.mostExpensiveSubTest.dereference()
          assert costliest.cost.value == 100.0
          context.respond(bool(true))
       }
   }

}