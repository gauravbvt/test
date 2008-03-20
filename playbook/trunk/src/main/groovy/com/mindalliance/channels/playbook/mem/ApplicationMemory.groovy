package com.mindalliance.channels.playbook.mem

import com.mindalliance.channels.playbook.ref.impl.ReferenceImpl
import com.mindalliance.channels.playbook.ref.Reference
import com.mindalliance.channels.playbook.ref.Referenceable
import com.opensymphony.oscache.base.CacheEntry
import com.mindalliance.channels.playbook.ifm.Channels
import com.mindalliance.channels.playbook.ifm.Project
import com.mindalliance.channels.playbook.ifm.Scenario
import com.opensymphony.oscache.base.Cache
import com.opensymphony.oscache.plugins.diskpersistence.DiskPersistenceListener
import com.opensymphony.oscache.base.Config
import com.opensymphony.oscache.base.NeedsRefreshException

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 10:06:15 AM
*/
class ApplicationMemory {

    public static final String ROOT_ID = 'CHANNELS'
    public static final String ROOT_DB = 'channels'
    public final static Reference ROOT = new ReferenceImpl(id: ROOT_ID, db:ROOT_DB)

    Cache cache

    ApplicationMemory() {
        boolean useMemoryCaching = true
        boolean unlimitedDiskCache = true
        boolean overflowPersistence = false
        boolean blocking = true
        String algorithmClass = 'com.opensymphony.oscache.base.algorithm.UnlimitedCache'
        int capacity = -1
        cache = new Cache(useMemoryCaching, unlimitedDiskCache, overflowPersistence, blocking, algorithmClass, capacity)
        DiskPersistenceListener listener = new DiskPersistenceListener()
        Config config = new Config()
        config.set('cache.path', "/home/jf/workspace/playbook/target/work/cache")    // TODO change this
        listener.configure(config)
        cache.setPersistenceListener(listener)
        initialize()
    }

    void storeAll(Collection<Referenceable> referenceables) {
        referenceables.each {store(it)}
    }

    void store(Referenceable referenceable) {
        cache.putInCache(referenceable.id, referenceable)
    }

    Referenceable retrieve(Reference ref) {
        Referenceable referenceable
        try {
            referenceable = (Referenceable) cache.getFromCache(ref.id, CacheEntry.INDEFINITE_EXPIRY)
        }
        catch (Exception e) {
            // Do nothing
            System.err.println(e)
            // TODO log warning
        }
        return referenceable
    }

    void clear(Reference ref) {
        if (ref != root) {
            cache.flushEntry(ref.id)
        }
    }

    void clearAll() {
        cache.clear()
        initializeContents()
    }

    private void initialize() {
        if (isEmpty()) {
            initializeContents()
        }
    }

    // Should get initialize contents from file?
    // MUST store a Referenceable with id = ROOT_ID and db = ROOT_DB
    private void initializeContents() {
        Channels channels = new Channels(about:"About Channels", project: new ReferenceImpl())
        channels.makeRoot()
        Project myProject = new Project(name:"My project", scenarios:[])
        store(myProject)
        channels.project = myProject.reference
        Scenario scenario = new Scenario(name:"My scenario")
        store(scenario)
        myProject.addScenario(scenario)
        store(channels)        
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