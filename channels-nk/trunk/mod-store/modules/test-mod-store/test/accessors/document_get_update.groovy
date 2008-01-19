import com.mindalliance.channels.nk.NetKernelCategory

use(NetKernelCategory) {
    context.subrequest("active:store_db", [type: 'delete', name: data('test_dbxml')])
    context.subrequest("active:store_db", [type: 'new', name: data('test_dbxml')])

    doc = '''
        <person beanClass="com.mindalliance.channels.data.beans.Person" id="1234" rooted="false"
                createdOn="Fri Jan 18 11:00:51 EST 2008">
            <firstName type="String">John</firstName>
            <middleName type="String">Q.</middleName>
            <lastName type="String">Public</lastName>
        </person>
    '''
    context.subrequest("active:store_doc", [type: 'new', db: data('test_dbxml'), id: data('1234'), doc: string(doc)])
    String got = context.sourceString("active:store_doc", [db: data('test_dbxml'), id: data('1234')])
    assert got =~ /Public/

    update = doc.replace('Public', 'Private')
    context.subrequest("active:store_doc", [type: 'sink', db: data('test_dbxml'), id: data('1234'), doc: string(update)])
    got = context.sourceString("active:store_doc", [db: data('test_dbxml'), id: data('1234')])
    assert got =~ /Private/

    context.subrequest("active:store_db", [type: 'delete', name: data('test_dbxml')])
    context.respond(bool(true))
}