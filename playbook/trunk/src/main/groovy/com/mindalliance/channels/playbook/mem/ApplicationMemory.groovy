package com.mindalliance.channels.playbook.mem

import com.mindalliance.channels.playbook.ref.impl.RefImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.opensymphony.oscache.base.CacheEntry
import com.opensymphony.oscache.base.Cache
import com.opensymphony.oscache.base.Config
import com.opensymphony.oscache.base.NeedsRefreshException
import com.mindalliance.channels.playbook.support.persistence.YamlPersistenceListener
import org.apache.log4j.Logger
import org.ho.yaml.YamlEncoder
import org.ho.yaml.YamlDecoder
import java.text.SimpleDateFormat
import com.mindalliance.channels.playbook.support.PlaybookApplication
import com.mindalliance.channels.playbook.support.Mapper
import com.mindalliance.channels.playbook.query.QueryManager
import com.mindalliance.channels.playbook.query.QueryCache
import com.mindalliance.channels.playbook.support.persistence.PlaybookCache
import com.mindalliance.channels.playbook.support.PlaybookSession

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
    static final String BACKUP_DIRECTORY = 'data/yaml/backup'

    static DEBUG = false
    static PlaybookCache cache
    private PlaybookApplication application

    private QueryCache queryCache = new QueryCache()

    ApplicationMemory(PlaybookApplication app) {
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
            cache = new PlaybookCache(useMemoryCaching, unlimitedDiskCache, overflowPersistence, blocking, algorithmClass, capacity)
            YamlPersistenceListener listener = new YamlPersistenceListener()
            Config config = new Config()
            config.set('cache.path', "./target/work/cache")
            listener.configure(config)
            cache.setPersistenceListener(listener)
        }
    }

    QueryCache getQueryCache() {
        return queryCache
    }

    void storeAll(Collection<Referenceable> referenceables) {
        referenceables.each {store(it)}
    }

    // always called within synchronized(this) block
    void lock(Ref ref) {
        cache.lock(ref, PlaybookSession.current())
    }

    // always called within synchronized(this) block
    boolean isLocked(Ref ref) {
        return cache.isLocked(ref, PlaybookSession.current())
    }

    // always called within synchronized(this) block
    void unlock(Ref ref) {
       cache.unlock(ref, PlaybookSession.current())
    }

    Ref store(Referenceable referenceable) {
        referenceable.beforeStore()
        cache.putInCache(referenceable.getId(), referenceable)
        if (DEBUG) Logger.getLogger(this.class.name).debug("==> to application: ${referenceable.type} $referenceable")
        referenceable.afterStore()
        QueryManager.modifiedInApplication(referenceable)     // update query cache
        return referenceable.reference
    }

    void deleteAll(Set<Ref> deletes) {
        deletes.each {delete(it) }
    }

    void delete(Ref ref) {
        use(NoSessionCategory) {
            QueryManager.modifiedInApplication(ref.deref())    // retrieve referenceable from application memory
        }
        cache.flushEntry(ref.id)
    }

    Referenceable retrieve(Ref ref) {
        Referenceable referenceable = null
        if (ref.isComputed()) {
            referenceable = ref.deref()
        }
        else {
            try {
                use(NoSessionCategory) {
                    referenceable = (Referenceable) cache.getFromCache(ref.id, CacheEntry.INDEFINITE_EXPIRY)
                    if (DEBUG) Logger.getLogger(this.class.name).debug("<== from application: ${referenceable.type} $referenceable")
                    referenceable.afterRetrieve()
                }
            }
            catch (Exception e) {
                // Do nothing
                Logger.getLogger(this.class.name).warn("Failed to retrieve $ref")
            }
        }
        return (Referenceable) referenceable // will be cloned by SessionMemory
    }

    boolean isFresh(Ref ref) {
        return cache.isFresh(ref)
    }

    void clearAll() {
        synchronized(this) {
            cache.clear()
        }
    }

    Ref getRoot() {
        return ROOT
    }

    // Creates a single YAML file named "<name>.yml" with ref and all that it references transitively
    int exportRef(Ref ref, String name) {
        String fileName = "${name}.yml"
        int count = 0
        File file = new File(EXPORT_DIRECTORY, fileName)
        if (file.exists()) { // make a backup
            backupFile(fileName, file)
        }
        File tempFile = new File(EXPORT_DIRECTORY, "_$fileName")
        FileOutputStream out = new FileOutputStream(tempFile)
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
            tempFile.renameTo(file)
            Logger.getLogger(this.class.name).info("Finished exporting $ref to $fileName ($count elements)")
        }
        catch (Exception e) {
            count == 0
            try {tempFile.delete()} catch (Exception exc) {}
            Logger.getLogger(this.class.name).error("Failed while exporting $ref", e)
        }
        finally {
            enc.close()
        }
        return count
    }

    private void backupFile(String name, File file) {
        Date now = new Date()
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss_z", Locale.getDefault());
        String timestamp = formatter.format(now);
        String bkName = "${name}_${timestamp}.bak"
        File copy = new File(BACKUP_DIRECTORY, bkName)
        FileWriter out = new FileWriter(copy)
        try {
            file.eachLine {line ->
                out.write(line)
            }
        }
        finally {
            out.close()
        }
    }

    // Create and store all elements serialized in file named <name>.yml
    int importRef(String name) {
        int count = 0
        File file = new File(EXPORT_DIRECTORY, "${name}.yml")
        if (!file.exists()) {
            Logger.getLogger(this.class.name).info("Import failed: unknown file $name")
        }
        else {
            FileInputStream input = new FileInputStream(file)
            YamlDecoder dec = new YamlDecoder(input)
            try {
                use(NoSessionCategory) {
                    while (true) {
                        Map map = (Map) dec.readObject()
                        Referenceable referenceable = (Referenceable) Mapper.fromMap(map)
                        store(referenceable)
                        count++
                    }
                }
            }
            catch (EOFException e) {
                Logger.getLogger(this.class.name).info("Finished importing $name ($count elements)")
            }
            finally {
                dec.close()
            }
        }
        return count
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