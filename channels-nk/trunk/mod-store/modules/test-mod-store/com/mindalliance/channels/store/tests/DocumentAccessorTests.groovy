package com.mindalliance.channels.store.tests

import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect
import com.mindalliance.channels.nk.aspects.IAspectPersistentBean
import com.mindalliance.channels.metamodel.TestBean
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.metamodel.TestRunComponent

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 20, 2008
 * Time: 4:42:14 PM
 * To change this template use File | Settings | File Templates.
 */
class DocumentAccessorTests {

    private Context context

    public DocumentAccessorTests(Context ctx) {
        context = ctx
    }

    void documentExistsCreateDelete() {
        TestBean test = new TestBean()
        test.name = 'A test'
        test.successful = false
        test.score = 100.0
        test.id = '1234'
        IAspectPersistentBean beanAspect = new PersistentBeanAspect(test)

        use(NetKernelCategory) {
            context.subrequest("active:store_db", [type: 'delete', name: data('test_dbxml')])
            context.subrequest("active:store_db", [type: 'new', name: data('test_dbxml')])

            boolean exists = context.isTrue("active:store_doc", [type: 'exists', db: data('test_dbxml'), id: data('1234')])
            assert !exists

            context.subrequest("active:store_doc", [type: 'new', db: data('test_dbxml'), id: data('1234'), doc: beanAspect])
            exists = context.isTrue("active:store_doc", [type: 'exists', db: data('test_dbxml'), id: data('1234')])
            assert exists

            context.subrequest("active:store_doc", [type: 'delete', db: data('test_dbxml'), id: data('1234')])
            exists = context.isTrue("active:store_doc", [type: 'exists', db: data('test_dbxml'), id: data('1234')])
            assert !exists

            context.subrequest("active:store_db", [type: 'delete', name: data('test_dbxml')])
            context.respond(bool(true))
        }
    }

    void documentGetUpdate() {
        use(NetKernelCategory) {
            context.subrequest("active:store_db", [type: 'delete', name: data('test_dbxml')])
            context.subrequest("active:store_db", [type: 'new', name: data('test_dbxml')])

            TestBean test = new TestBean(id: '1234', name: 'A test', successful: false, score: 100.0)
            test.runs.add(new TestRunComponent(date: new Date(), tester: 'John Doe'))
            test.runs.add(new TestRunComponent(date: new Date(), tester: 'Jane Q. Public'))
            IAspectPersistentBean beanAspect = new PersistentBeanAspect(test)
            context.subrequest("active:store_doc", [type: 'new', db: data('test_dbxml'), id: data('1234'), doc: beanAspect])
            String got = context.sourceString("active:store_doc", [db: data('test_dbxml'), id: data('1234')])
            assert got =~ /A test/
            
            test.name = 'A great test'
            beanAspect = new PersistentBeanAspect(test)
            context.subrequest("active:store_doc", [type: 'sink', db: data('test_dbxml'), id: data(test.id), doc: beanAspect])
            got = context.sourceString("active:store_doc", [db: data('test_dbxml'), id: data(test.id)])
            assert got =~ /A great test/
            
            context.subrequest("active:store_doc", [type: 'delete', db: data('test_dbxml'), id: data(test.id)])
            boolean exists = context.isTrue("active:store_doc", [type: 'exists', db: data('test_dbxml'), id: data(test.id)])
            assert !exists
            
            context.subrequest("active:store_db", [type: 'delete', name: data('test_dbxml')])
            context.respond(bool(true))
        }
    }

}