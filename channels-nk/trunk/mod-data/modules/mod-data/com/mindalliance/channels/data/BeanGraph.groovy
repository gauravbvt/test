package com.mindalliance.channels.data

import com.mindalliance.channels.nk.bean.IPersistentBean
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.IStoreAdaptor
import com.mindalliance.channels.data.adaptors.StoreAdaptor
import com.mindalliance.channels.nk.NetKernelCategory
import groovy.xml.MarkupBuilder
import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.nk.IStoreAdaptor
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect
import com.mindalliance.channels.nk.bean.IPersistentBean

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 14, 2008
* Time: 4:32:36 PM
* To change this template use File | Settings | File Templates.
*/
class BeanGraph {

    private Map<String, Map<String, IPersistentBean>> cache = new HashMap<String, Map<String, IPersistentBean>>() // db ->* id -> bean
    private List dbs = [ ] // List of dbs (names) known to have been opened - and created and initialized if needed

    void refresh() {
        Map<String, Map<String, IPersistentBean>> fresh = new HashMap<String, Map<String, IPersistentBean>>()
        cache.each {db, dbCache ->
            fresh[db] = new HashMap<String, IPersistentBean>()
            dbCache.each { id, bean ->
                if (bean.isRooted()) fresh[db][id] = bean
            }
        }
        cache = fresh
    }

    String search(String beanClass, String db, String id, String queryUri, Context context) {
        String xml
        use(NetKernelCategory, PersistenBeanCategory) {
            if (queryUri.endsWith('.groovy')) {
                String query = context.sourceString(queryUri)
                StringWriter writer = new StringWriter()
                MarkupBuilder builder = new MarkupBuilder(writer)
                Eval.me('args', [bean: bean, builder: builder], query)
                xml = writer.toString()
            }
            else {
                throw new IllegalArgumentException("Unsupported query type for $queryUri")
            }
        }
        return xml

    }

    IPersistentBean retrieveBean(String db, String id, Context context) {
        IPersistentBean bean
        use(PersistentBeanCategory) {
            if (!(bean = fromCache(db, id))) {
                IStoreAdaptor storeAdaptor = selectAdaptorFor(db, context)
                bean = context.getPersistentBean(storeAdaptor.retrieve(db, id, context))
                cache(db, id, bean)
            }
        }
        return bean
    }

    void storeBean(IPersistentBean bean, Context context) {
        use(NetKernelCategory) {
            IStoreAdaptor storeAdaptor = selectAdaptorFor(bean.db, context)
            storeAdaptor.persist(bean.db, bean.id, new PersistentBeanAspect(bean), context)
            cache(bean.db, bean.id, bean)
        }
    }

    void removeBean(String db, String id, Context context) {
        use(NetKernelCategory) {
            IStoreAdaptor storeAdaptor = selectAdaptorFor(db, context)
            storeAdaptor.remove(db, id, context)
            uncache(bean.db, bean.id)
        }
    }

    boolean isBeanExists(String db, String id, Context context) {
        boolean exists
        if (!(exists = isCached(db, id))) {
            IStoreAdaptor storeAdaptor = selectAdaptorFor(db, context)
            exists = storeAdaptor.exists(db, id, context)
        }
        return exists
    }

    // Select an adaptor for the named db. Make sure it is opened and loaded with initial content, if any
    IStoreAdaptor selectAdaptorFor(String db, Context context) {
        IStoreAdaptor storeAdaptor = new StoreAdaptor() // TODO replace by some adaptor selection logic based on db
        if (!dbs.contains(db)) {
            boolean created = storeAdaptor.open(db, context)    // open, create+load if needed
            if (created) {
                use(NetKernelCategory) {
                   String initContentUri = "db:${db}.xml"
                   if (context.exists(initContentUri)) {
                       storeAdapter.load(db, initContentUri, context) 
                   }
                }
            }
            dbs.add(db)
        }
        return storeAdaptor
    }

    boolean isCached(String db, String id) {
        Map<String, IPersistentBean> dbCache = dbCache(db)
        return dbCache.containsKey(id)
    }

    void uncache(String db, String id) {
        Map<String, IPersistentBean> dbCache = dbCache(db)
        dbCache.remove(id)
    }

    void cache(String db, String id, IPersistentBean bean) {
        Map<String, IPersistentBean> dbCache = dbCache(db)
        dbCache[id] = bean
    }

    IPersistentBean fromCache(String db, String id) {
        Map<String, IPersistentBean> dbCache = dbCache(db)
        return dbCache[id]
    }

    private Map<String, IPersistentBean> dbCache(db) {
        if (!cache[db]) {
            cache[db] = new HashMap<String, IPersistentBean>()
        }
        return cache[db]
    }

}