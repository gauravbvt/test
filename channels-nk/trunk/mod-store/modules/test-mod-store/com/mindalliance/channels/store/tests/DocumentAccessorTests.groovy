package com.mindalliance.channels.store.tests

import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect
import com.mindalliance.channels.nk.aspects.IAspectPersistentBean
import com.mindalliance.channels.metamodel.TestBean
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.metamodel.TestRunComponent
import com.mindalliance.channels.nk.bean.SimpleData

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
        test.id = '1234'
        test.name = SimpleData.from('A test')
        test.kind = SimpleData.from('Unit')
        test.successful = SimpleData.from(false)
        test.score = SimpleData.from(100.0)
        IAspectPersistentBean beanAspect = new PersistentBeanAspect(test)

        use(NetKernelCategory) {
            context.subrequest("active:store_db", [type: 'new', name: data('test_dbxml')])

            boolean exists = context.isTrue("active:store_doc", [type: 'exists', db: data('test_dbxml'), id: data('1234')])
            assert !exists

            context.subrequest("active:store_doc", [type: 'new', db: data('test_dbxml'), id: data('1234'), doc: beanAspect])
            exists = context.isTrue("active:store_doc", [type: 'exists', db: data('test_dbxml'), id: data('1234')])
            assert exists

            context.subrequest("active:store_db", [type: 'delete', contents: bool(true), name: data('test_dbxml')])
            exists = context.isTrue("active:store_doc", [type: 'exists', db: data('test_dbxml'), id: data('1234')])
            assert !exists

            context.respond(bool(true))
        }
    }

    void documentGetUpdate() {
        use(NetKernelCategory) {
            context.subrequest("active:store_db", [type: 'new', name: data('test_dbxml')])

            TestBean test = new TestBean(id: '1234')
            test.name = SimpleData.from('A test')
            assert test.name.value.equals('A test')
            test.successful = SimpleData.from(false)
            test.score = SimpleData.from(100.0)
            test.runs.add(new TestRunComponent(date: SimpleData.from(new Date()), tester: SimpleData.from('John Doe')))
            test.runs.add(new TestRunComponent(date: SimpleData.from(new Date()), tester: SimpleData.from('Jane Q. Public')))
            IAspectPersistentBean beanAspect = new PersistentBeanAspect(test)
            context.subrequest("active:store_doc", [type: 'new', db: data('test_dbxml'), id: data('1234'), doc: beanAspect])
            String got = context.sourceString("active:store_doc", [db: data('test_dbxml'), id: data('1234')])
            assert got =~ /A test/
            
            test.name = SimpleData.from('A great test')
            beanAspect = new PersistentBeanAspect(test)
            context.subrequest("active:store_doc", [type: 'sink', db: data('test_dbxml'), id: data(test.id), doc: beanAspect])
            got = context.sourceString("active:store_doc", [db: data('test_dbxml'), id: data(test.id)])
            assert got =~ /A great test/
            
            context.subrequest("active:store_db", [type: 'delete', contents: bool(true), name: data('test_dbxml')])
            boolean exists = context.isTrue("active:store_doc", [type: 'exists', db: data('test_dbxml'), id: data(test.id)])
            assert !exists
            
            context.respond(bool(true))
        }
    }

}