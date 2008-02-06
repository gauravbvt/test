package com.mindalliance.channels.data.adaptors

import com.mindalliance.channels.nk.IStoreAdaptor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.mindalliance.channels.nk.NetKernelCategory
import com.ten60.netkernel.urii.IURAspect
import com.ten60.netkernel.urii.IURRepresentation

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 16, 2008
* Time: 3:37:18 PM
* To change this template use File | Settings | File Templates.
*/
class StoreAdaptor implements IStoreAdaptor {

    boolean open(String db, INKFConvenienceHelper context) {
        boolean exists
        use(NetKernelCategory) {
            exists = context.isTrue("active:store_db", [type: 'exists', name: data(db)])
            if (!exists) {
                context.subrequest("active:store_db", [type: 'new', name: data(db)])
                context.log("Automatically created container $db", 'info')
            }
        }
        return !exists
    }

    void load(String db, String contentUri, INKFConvenienceHelper context) {
        use(NetKernelCategory) {
            context.subrequest("active:store_db", [type: 'sink', name: data(db), load: contentUri])
        }
    }

     void close(String db, INKFConvenienceHelper context) {

     }

     void deleteStore(String db, INKFConvenienceHelper context) {
         use(NetKernelCategory) {
              context.subrequest("active:store_db", [type: 'delete', name: data(db)])
         }         
     }

    void emptyStore(String db, INKFConvenienceHelper context) {
         use(NetKernelCategory) {
              context.subrequest("active:store_db", [type: 'delete', contents: bool(true), name: data(db)])
         }
     }
     boolean storeExists(String db, INKFConvenienceHelper context) {
         boolean exists
         use(NetKernelCategory) {
               exists = context.isTrue("active:store_db", [type: 'exists', name: data(db)])
         }
         return exists
     }


    void persist(String db, String id, IURAspect aspect, INKFConvenienceHelper context) {
        use(NetKernelCategory) {
           def type = exists(db, id, context) ? 'sink' : 'new'
               context.subrequest("active:store_doc",
                                  [type: type, db: data(db), id: data(id),
                                   doc: aspect])
        }
    }

    // Initialize from store
    IURRepresentation retrieve(String db, String id, INKFConvenienceHelper context) {
        return context.subrequest("active:store_doc",
                                    [type:'source', db: data(db), id: data(id)])
    }

    boolean exists(String db, String id, INKFConvenienceHelper context) {
        boolean exists
        use(NetKernelCategory) {
           exists = context.isTrue("active:store_doc", [type: 'exists', db: data(db), id: data(id)])
        }
        return exists
    }

    void remove(String db, String id, INKFConvenienceHelper context) {
        use(NetKernelCategory) {
           context.subrequest("active:store_doc", [type: 'delete', db: data(db), id: data(id)])
        }
    }

}