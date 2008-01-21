import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.metamodel.*
import com.mindalliance.channels.nk.bean.IPersistentBean

use (NetKernelCategory) {

  context.subrequest("active:store_db", [type: 'delete', name: data('test')])
    IPersistentBean test = new TestBean(name: 'Unit test', successful: false, score: '0.75')
    IPersistentBean parentTest = new TestBean(id: '1111', name: 'Integration test', successful: true, score: '1.0', rooted: true)
   /*    test.parent.id = parentTest.id


    BeanXMLConverter converter = new BeanXMLConverter(context)
    String testDoc = converter.xmlFromBean(test)
    String parentTestDoc = converter.xmlFromBean(parentTest)
    context.subrequest("active:data_bean", [type: 'new', db:data('test'), doc:string(testDoc) ])
    context.subrequest("active:data_bean", [type: 'new', db:data('test'), doc:string(parentTestDoc) ])
*/
    context.respond(bool(true))
    // Don't delete -- content of db needed for next test
}
