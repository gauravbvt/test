package com.mindalliance.channels.playbook.mem

import com.mindalliance.channels.playbook.ref.impl.RefImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.opensymphony.oscache.base.CacheEntry
import com.opensymphony.oscache.base.Cache
import com.opensymphony.oscache.plugins.diskpersistence.DiskPersistenceListener
import com.opensymphony.oscache.base.Config
import com.opensymphony.oscache.base.NeedsRefreshException
import org.apache.wicket.Application

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 10:06:15 AM
*/
class ApplicationMemory implements Serializable {

    public static final String ROOT_ID = 'CHANNELS'
    public static final String ROOT_DB = 'channels'
    public final static Ref ROOT = new RefImpl(id: ROOT_ID, db: ROOT_DB)

    static Cache cache
    private Application application

    ApplicationMemory(Application app) {
        application = app
        if (!cache) ApplicationMemory.initializeCache()
    }

    static synchronized void initializeCache() {
        if (!cache) {  // in the highly unlikely event of a race condition, test again
            boolean useMemoryCaching = true
            boolean unlimitedDiskCache = true
            boolean overflowPersistence = false
            boolean blocking = true
            String algorithmClass = 'com.opensymphony.oscache.base.algorithm.UnlimitedCache'
            int capacity = -1
            cache = new Cache(useMemoryCaching, unlimitedDiskCache, overflowPersistence, blocking, algorithmClass, capacity)
            DiskPersistenceListener listener = new DiskPersistenceListener()
            Config config = new Config()
            config.set('cache.path', "./target/work/cache") // TODO change this
            listener.configure(config)
            cache.setPersistenceListener(listener)
        }
    }

    void storeAll(Collection<Referenceable> referenceables) {
        referenceables.each {store(it)}
    }

    Ref store(Referenceable referenceable) {
        referenceable.beforeStore()
        cache.putInCache(referenceable.getId(), referenceable)
        referenceable.afterStore()
        return referenceable.reference
    }

    void deleteAll(Set<Ref> deletes) {
        deletes.each {delete(it) }
    }

    void delete(Ref ref) {
        cache.flushEntry(ref.id)
    }

    Referenceable retrieve(Ref ref) {
        Referenceable referenceable
        try {
            referenceable = (Referenceable) cache.getFromCache(ref.id, CacheEntry.INDEFINITE_EXPIRY)
            referenceable.afterRetrieve()
        }
        catch (Exception e) {
            // Do nothing
            System.err.println(e)
            // TODO log warning
        }
        return (Referenceable) referenceable // will be cloned by SessionMemory
    }

    void clear(Ref ref) {
        if (ref != root) {
            cache.flushEntry(ref.id)
        }
    }

    void clearAll() {
        cache.clear()
        initializeContents()
    }

    Ref getRoot() {
        return ROOT
    }

    private void initialize() {
        if (isEmpty()) {
            initializeContents()
        }
    }

    // Should get initialize contents from file?
    // MUST store a Referenceable with id = ROOT_ID and db = ROOT_DB
    private void initializeContents() {
        application.initializeContents()
    }

    private boolean isEmpty() {
        boolean empty = true
        try {
            def root = cache.getFromCache(ROOT_ID, CacheEntry.INDEFINITE_EXPIRY)
            empty = false
        }
        catch (NeedsRefreshException nre) {
            // TODO use logger
            System.out.println("Cache is empty")
        }
        return empty
    }
}