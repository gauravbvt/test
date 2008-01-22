package com.mindalliance.channels.data.test
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect
import com.mindalliance.channels.nk.aspects.IAspectPersistentBean
import com.mindalliance.channels.metamodel.TestBean
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.metamodel.TestRunComponent

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 21, 2008
 * Time: 2:48:26 PM
 * To change this template use File | Settings | File Templates.
 */
class MemoryAccessorTests {

   Context context

    MemoryAccessorTests(Context ctx) {
        context = ctx
    }

        void dataMemorySinkBeans() {
        use(NetKernelCategory) {
            String count = context.sourceString("active:data_memory", [type: 'sink', db: data('test_dbxml'), beans: 'ffcpl:/fixtures/testBeans.xml'])
            assert new Integer(count) == 4
            boolean exists = context.isTrue("active:data_bean", [type: 'exists', db: data('test_dbxml'), id: data('Top')])
            assert exists
            exists = context.isTrue("active:data_bean", [type: 'exists', db: data('test_dbxml'), id: data('SubA')])
            assert exists
            exists = context.isTrue("active:data_bean", [type: 'exists', db: data('test_dbxml'), id: data('SubB')])
            assert exists
            exists = context.isTrue("active:data_bean", [type: 'exists', db: data('test_dbxml'), id: data('SubSub')])
            assert exists
            context.respond(bool(true))
        }
    }

    void dataMemorySearch() {

    }

    void dataMemoryRefresh() {
        
    }

}