package com.mindalliance.channels.data

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import groovy.util.slurpersupport.GPathResult
import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.bean.IPersistentBean

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 16, 2008
* Time: 10:09:24 AM
* To change this template use File | Settings | File Templates.
*/
// Singleton class
class BeanMemory {

    static private BeanMemory instance

    private BeanGraph beanGraph // dbName -> beanGraph. One bean graph per database. A bean graph is lazily loaded in memory from its database.
    // private WorkingMemory workingMemory // DROOLS

    static BeanMemory getInstance() {
        if (!instance) {
            instance = new BeanMemory()
        }
        return instance
    }

    private BeanMemory() {
        beanGraph = new BeanGraph()
        // workingMemory = new ...
    }

    static String makeGUID(Context context) {
        GPathResult guid
        use(NetKernelCategory) {
           guid = context.getXml("active:guid", [:])
        }
        return guid.text()
    }

    void refresh() {
        beanGraph.refresh()
        // do something else?
    }

    String search(String beanClass, String db, String id, String queryUri, Context context) {
        return beanGraph.search(beanClass, db, id, queryUri, context)
    }

    IPersistentBean retrieveBean(String db, String id, Context context)  {
        IPersistentBean bean = beanGraph.retrieveBean(db, id, context)
        // do something else?
        return bean
    }

    void updateBean(IPersistentBean bean, Context context) {
        storeBean(bean, context)
        // do something else?
    }

    void newBean(IPersistentBean bean, Context context) {
        storeBean(bean, context)
        // do something else?
    }

    void removeBean(String db, String id, Context context) {
        beanGraph.removeBean(db, id, context)
        // do something else?
    }

    boolean isBeanExists(String db, String id, Context context) {
        return beanGraph.isBeanExists(db, id, context)
    }

    private void storeBean(IPersistentBean bean, Context context) {
        beanGraph.storeBean(bean, context)
    }


}