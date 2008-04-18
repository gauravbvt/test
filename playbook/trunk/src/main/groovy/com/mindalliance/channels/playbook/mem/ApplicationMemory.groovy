package com.mindalliance.channels.playbook.mem

import com.mindalliance.channels.playbook.ref.impl.RefImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.opensymphony.oscache.base.CacheEntry
import com.opensymphony.oscache.base.Cache
import com.opensymphony.oscache.base.Config
import com.opensymphony.oscache.base.NeedsRefreshException
import org.apache.wicket.Application
import com.mindalliance.channels.playbook.support.persistence.YamlPersistenceListener
import org.apache.log4j.Logger
import org.ho.yaml.YamlEncoder
import org.ho.yaml.YamlDecoder
import com.mindalliance.channels.playbook.ref.impl.BeanImpl

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2008
 * Time: 10:06:15 AM
 */
class ApplicationMemory implements Serializable {

    static final String ROOT_ID = 'CHANNELS'
    static final String ROOT_DB = 'channels'
    static final Ref ROOT = new RefImpl(id: ROOT_ID, db: ROOT_DB)

    static final String EXPORT_DIRECTORY = 'data/yaml'

    static DEBUG = false
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
            YamlPersistenceListener listener = new YamlPersistenceListener()
            Config config = new Config()
            config.set('cache.path', "./target/work/cache")
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
        if (DEBUG) Logger.getLogger(this.class.name).debug("==> to application: ${referenceable.type} $referenceable")
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
        Referenceable referenceable = null
        try {
            referenceable = (Referenceable) cache.getFromCache(ref.id, CacheEntry.INDEFINITE_EXPIRY)
            if (DEBUG) Logger.getLogger(this.class.name).debug("<== from application: ${referenceable.type} $referenceable")
            referenceable.afterRetrieve()
        }
        catch (Exception e) {
            // Do nothing
            Logger.getLogger(this.class.name).warn("Failed to retrieve $ref")
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

    // Creates a single YAML file named "<name>.yml" with ref and all that it references transitively
    int exportRef(Ref ref, String name) {
        int count = 0
        File file = new File(EXPORT_DIRECTORY, "${name}.yml")
        if (file.exists()) { // make a backup
            backupFile(file)
        }
        FileOutputStream out = new FileOutputStream(file)
        YamlEncoder enc = new YamlEncoder(out)
        List<Ref> queue = [ref]
        Set<Ref> exported = new HashSet<Ref>()
        try {
            while (queue) {
                Ref next = (Ref) queue.pop()
                exported.add(next)
                Referenceable referenceable = retrieve(next)
                referenceable.references().each {aRef ->
                    if (!exported.contains(aRef)) {
                        queue.add(aRef)
                    }
                }
                enc.writeObject(referenceable.toMap())
                count++
            }
            enc.flush()
            Logger.getLogger(this.class.name).info("Finished exporting ${ref.deref()} to $name ($count elements)")
        }
        catch (Exception e) {
            Logger.getLogger(this.class.name).error("Failed while exporting $name", e)
        }
        finally {
            enc.close()
        }
        return count
    }

    private void backupFile(File file) {
        Date now = new Date()
        String bkName = "${file.getAbsolutePath()}_${now}_.bak"
        FileWriter copy = new FileWriter(bkName)
        try {
            file.eachLine {line ->
                copy.write(line)
            }
        }
        finally {
            copy.close()
        }
    }

    // Create and store all elements serialized in file named <name>.yml
    int importRef(String name) {
        int count = 0
        File file = new File(EXPORT_DIRECTORY, "${name}.yml")
        if (!file.exists()) {
            throw new IllegalArgumentException("Import failed: unknown file $name")
        }
        FileInputStream input = new FileInputStream(file)
        YamlDecoder dec = new YamlDecoder(input)
        try {
            while (true) {
                Referenceable referenceable = (Referenceable)BeanImpl.fromMap((Map) dec.readObject())
                store(referenceable)
                count++
            }
        }
        catch (EOFException e) {
            Logger.getLogger(this.class.name).info("Finished importing $name ($count elements)")
        }
        finally {
            dec.close()
        }
        return count
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
            cache.getFromCache(ROOT_ID, CacheEntry.INDEFINITE_EXPIRY)
            empty = false
        }
        catch (NeedsRefreshException nre) {
            Logger.getLogger(this.class.name).info("Cache is empty")
        }
        return empty
    }

}