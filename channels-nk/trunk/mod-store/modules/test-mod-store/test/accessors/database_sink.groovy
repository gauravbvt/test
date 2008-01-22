// Load documents from file into a database

import com.mindalliance.channels.nk.NetKernelCategory

use(NetKernelCategory) {
    context.subrequest("active:store_db", [type: 'delete', name: data('test_dbxml')])
    context.subrequest("active:store_db", [type: 'new', name: data('test_dbxml')])
    context.subrequest("active:store_db", [type: 'sink', name: data('test_dbxml'), load: 'ffcpl:/fixtures/test_dbxml.xml'])
    String xml = context.sourceString("active:store_db", [name: data('test_dbxml')] )
    context.subrequest("active:store_db", [type: 'delete', name: data('test_dbxml')])
    context.respond(string(xml))
}