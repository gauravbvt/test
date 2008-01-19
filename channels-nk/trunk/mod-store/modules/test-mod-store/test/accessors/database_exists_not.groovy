import com.mindalliance.channels.nk.NetKernelCategory

use (NetKernelCategory) {
    context.subrequest("active:store_db", [type: 'exists', name: data('nosuchdatabase_dbxml'), mimeType: 'text/xml'])
}

