package com.mindalliance.channels.data.adaptors

import com.mindalliance.channels.nk.channels.IStoreAdaptor
import com.mindalliance.channels.nk.channels.IPersistentBean
import groovy.util.slurpersupport.GPathResult
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.mindalliance.channels.data.BeanXMLConverter
import com.mindalliance.channels.nk.NetKernelCategory

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
            exists = context.isTrue("active:store_db", [type: 'exists', name: db])
            if (!exists) {
                context.subrequest("active:store_db", [type: 'new', name: db])
            }
        }
        return !exists
    }

    void load(String db, String contentUri, INKFConvenienceHelper context) {
        use(NetKernelCategory) {
            context.subrequest("active:store_db", [type: 'sink', name: db, load: contentUri])
        }
    }

     void close(String db, INKFConvenienceHelper context) {

     }


    void persist(IPersistentBean bean, INKFConvenienceHelper context) {
        BeanXMLConverter converter = new BeanXMLConverter(context)
        String xml = converter.xmlFromBean(bean)
        use(NetKernelCategory) {
           def type = exists(bean.db, bean.id, context) ? 'sink' : 'new'
               context.subrequest("active:store_doc",
                                  [type: type, db: data(bean.db), id: data(bean.id),
                                   doc: string(xml)])
        }
    }

    // Initialize from store
    void reify(IPersistentBean bean, INKFConvenienceHelper context) {
        assert bean.id.size() != 0
        assert bean.db.size() != 0
        GPathResult xml = retrieveXml(context)
        BeanXMLConverter converter = new BeanXMLConverter(context)
        converter.initBeanFromXml(bean, xml)
    }

    boolean exists(String db, String id, INKFConvenienceHelper context) {
        boolean exists
        use(NetKernelCategory) {
           exists = context.isTrue("active:store_doc", [type: 'exists', db: data(bean.db), id: data(bean:id)])
        }
        return exists
    }

    void remove(String db, String id, INKFConvenienceHelper context) {
        use(NetKernelCategory) {
           context.subrequest("active:store_doc", [type: 'delete', db: data(bean.db), id: data(bean:id)])
        }
    }

    private GPathResult retrieveXml(INKFConvenienceHelper context) {
        GPathResult xml
        use(NetKernelCategory) {
            xml = context.getXml("active:store_doc", [
                    type: SOURCE,
                    db: data(db),
                    id: data(id)])
        }

        return xml
    }


}