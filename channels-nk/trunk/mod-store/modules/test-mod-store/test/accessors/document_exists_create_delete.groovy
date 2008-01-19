import com.mindalliance.channels.nk.NetKernelCategory

use(NetKernelCategory) {
    context.subrequest("active:store_db", [type: 'delete', name: data('test_dbxml')])
    context.subrequest("active:store_db", [type: 'new', name: data('test_dbxml')])

    boolean exists = context.isTrue("active:store_doc", [type: 'exists', db: data('test_dbxml'), id: data('1234')])
    assert !exists
    doc = '''
        <person beanClass="com.mindalliance.channels.data.beans.Person" id="1234" rooted="false"
                createdOn="Fri Jan 18 11:00:51 EST 2008">
            <firstName type="String">John</firstName>
            <middleName type="String">Q.</middleName>
            <lastName type="String">Public</lastName>
        </person>
    '''
    context.subrequest("active:store_doc", [type: 'new', db: data('test_dbxml'), id: data('1234'), doc: string(doc)])
    exists = context.isTrue("active:store_doc", [type: 'exists', db: data('test_dbxml'), id: data('1234')])
    assert exists
    context.subrequest("active:store_doc", [type: 'delete', db: data('test_dbxml'), id: data('1234')])
    exists = context.isTrue("active:store_doc", [type: 'exists', db: data('test_dbxml'), id: data('1234')])
    assert !exists
    context.subrequest("active:store_db", [type: 'delete', name: data('test_dbxml')])
    context.respond(bool(true))
}
