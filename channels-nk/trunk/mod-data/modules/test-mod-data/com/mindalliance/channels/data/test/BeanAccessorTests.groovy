package com.mindalliance.channels.data.test

import com.mindalliance.channels.nk.NetKernelCategory
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.data.util.PersistentBeanCategory
import com.mindalliance.channels.nk.bean.SimpleData


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
            String xml = context.sourceString('ffcpl:/fixtures/testbean1.xml')
            String id = context.sourceString("active:data_bean", [type: 'new', db: data('test_dbxml'), id: data('1234'), bean: string(xml)])
            assert id == '1234'
            context.respond(bool(true))
            // Leave bean in memory
        }
    }

    void dataBeanGetUpdateDelete() {
        use(NetKernelCategory, PersistentBeanCategory) {
            String xml = context.sourceString('ffcpl:/fixtures/testbean1.xml')
            String id = context.sourceString("active:data_bean", [type: 'new', db: data('test_dbxml'), id: data('1234'), bean: string(xml)])

            IPersistentBean bean = context.sourcePersistentBean("active:data_bean", [db: data('test_dbxml'), id: data(id)])
            assert bean.name.value == 'A test'
            bean.name = SimpleData.from('A great test')
            context.subrequest("active:data_bean", [type: 'sink', db: data('test_dbxml'), id: data('1234'), bean: persistentBean(bean)])
            bean = context.sourcePersistentBean("active:data_bean", [db: data('test_dbxml'), id: data('1234')])
            assert bean.name.value == 'A great test'

            context.subrequest("active:data_bean", [type: 'delete', db: data('test_dbxml'), id: data('1234')])
            boolean exists = context.isTrue("active:data_bean", [type: 'exists', db: data('test_dbxml'), id: data('1234')])
            assert !exists

            context.respond(bool(true))
        }
    }



}