package com.mindalliance.channels.data.test
import com.mindalliance.channels.nk.NetKernelCategory
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 21, 2008
 * Time: 2:48:26 PM
 * To change this template use File | Settings | File Templates.
 */
class MemoryAccessorTests {

   static private final String DB = 'test_dbxml'

   Context context

    MemoryAccessorTests(Context ctx) {
        context = ctx
    }

    // SINK
   void dataMemorySinkBeans() {
        use(NetKernelCategory) {
            String count = context.sourceString("active:data_memory", [type: 'sink', db: data(DB), beans: 'ffcpl:/fixtures/testBeans.xml'])
            assert new Integer(count) == 4
            boolean exists = context.isTrue("active:data_bean", [type: 'exists', db: data(DB), id: data('Top')])
            assert exists
            exists = context.isTrue("active:data_bean", [type: 'exists', db: data(DB), id: data('SubA')])
            assert exists
            exists = context.isTrue("active:data_bean", [type: 'exists', db: data(DB), id: data('SubB')])
            assert exists
            exists = context.isTrue("active:data_bean", [type: 'exists', db: data(DB), id: data('SubSub')])
            assert exists
            context.respond(bool(true))
        }
    }

    // NEW
    void dataMemoryRefresh() {
        use(NetKernelCategory) {
          context.subrequest("active:data_memory", [type: 'new', db: data(DB)])
                 context.respond(bool(true))
        }
    }

    // EXIST
    void dataMemoryExistsDB() {
        use(NetKernelCategory) {
          context.subrequest("active:store_db", [type: 'new', name: data(DB)])
          boolean exists = context.isTrue("active:data_memory", [type: 'exists', db: data(DB)])
          assert exists
            context.respond(bool(true))
        }
     }

    // DELETE
    void dataMemoryDeleteDB() {
        use(NetKernelCategory) {
          context.subrequest("active:data_memory", [type: 'delete', db: data(DB)])
          boolean exists = context.isTrue("active:store_db", [type: 'exists', name: data(DB)])
          assert !exists
            context.respond(bool(true))
        }
    }


    // SOURCE
    void dataMemorySearch() {
        use(NetKernelCategory) {
            // Delete db
          context.subrequest("active:data_memory", [type: 'delete', db: data(DB)])
          // Add beans
          context.subrequest("active:data_memory", [type: 'sink', db: data(DB), beans: 'ffcpl:/fixtures/testBeans.xml'])
          // Refresh memory (to force lazy reloads from db)
          context.subrequest("active:data_memory", [type: 'new', db: data(DB)])
          // Query = "Find all successful sub-tests of the Top test"
          String xml = context.sourceString("active:data_memory", [db: data(DB), id: data('Top'), query: 'ffcpl:/fixtures/testBeansQuery.groovy'])
          assert xml =~ /SubB/
          assert xml =~ /SubSub/
            context.respond(bool(true))
        }
    }


}