import com.mindalliance.channels.nk.NetKernelCategory

use (NetKernelCategory) {
    // make sure it's not there
    context.subrequest("active:store_db", [type: 'delete', name: data('test_dbxml')])
    boolean exists = context.isTrue("active:store_db", [type: 'exists', name: data('test_dbxml')])
    assert !exists
    // create
    context.subrequest("active:store_db", [type: 'new', name: data('test_dbxml')])
    exists = context.isTrue("active:store_db", [type: 'exists', name: data('test_dbxml')])
    assert exists
    // delete
    context.subrequest("active:store_db", [type: 'delete', name: data('test_dbxml')])
    exists = context.isTrue("active:store_db", [type: 'exists', name: data('test_dbxml')])
    assert !exists
    
    context.respond(bool(true))
}