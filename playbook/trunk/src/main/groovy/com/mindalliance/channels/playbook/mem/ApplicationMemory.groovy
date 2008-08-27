package com.mindalliance.channels.playbook.mem

import com.mindalliance.channels.playbook.ref.impl.RefImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.opensymphony.oscache.base.CacheEntry
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
import com.mindalliance.channels.playbook.support.drools.RuleBaseSession
import com.mindalliance.channels.playbook.support.persistence.Mappable

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2008
 * Time: 10:06:15 AM
 */
class ApplicationMemory implements Serializable {
    private static final long serialVersionUID = -1L;

    static final String ROOT_ID = 'CHANNELS'
    static final String ROOT_DB = 'channels'

    static final String RULES_PACKAGE = '/com/mindalliance/channels/playbook/rules/channels.rules'

    static DEBUG = false
    private PlaybookCache cached
    static RuleBaseSession ruleBaseSession
    private PlaybookApplication application

    private QueryCache queryCache = new QueryCache()

    ApplicationMemory(PlaybookApplication app) {
        application = app
        // if (!ruleBaseSession) ApplicationMemory.initializeRuleBaseSession()
    }

    private synchronized PlaybookCache getCache() {
        if (!cached) {  // in the highly unlikely event of a race condition, test again
            boolean useMemoryCaching = true
            boolean unlimitedDiskCache = true
            boolean overflowPersistence = false
            boolean blocking = true
            String algorithmClass = 'com.opensymphony.oscache.base.algorithm.UnlimitedCache'
            int capacity = -1
            cached = new PlaybookCache(useMemoryCaching, unlimitedDiskCache, overflowPersistence, blocking, algorithmClass, capacity)
            YamlPersistenceListener listener = new YamlPersistenceListener()
            Config config = new Config()
            config.set('cache.path', getCacheDirectory() )
            listener.configure(config)
            cached.setPersistenceListener(listener)
        }
        return cached;
    }

/*    static synchronized void initializeRuleBaseSession() {
        if (!ruleBaseSession) {
            ruleBaseSession = new RuleBaseSession(RULES_PACKAGE)
        }
    }*/

    RuleBaseSession getRuleBaseSession() {
        synchronized (this) {
            if (ruleBaseSession == null) {
                ruleBaseSession = new RuleBaseSession(RULES_PACKAGE)
            }
        }
        return ruleBaseSession
    }

    static private String getExportDirectory() {
        String dirPath = PlaybookApplication.get().servletContext.getInitParameter("persistence-dir") ?: 'data/yaml/'
    }

    static private String getCacheDirectory() {
        getExportDirectory() + "cache/"
    }

    static private String getBackupDirectory() {
        getExportDirectory() + "backup/"
    }

    QueryCache getQueryCache() {
        return queryCache
    }

    void storeAll(Collection<Referenceable> referenceables) {
        referenceables.each {doStore(it)}
        getRuleBaseSession().fireAllRules()
    }

    boolean lock(Ref ref) {
        return cache.lock(ref)
    }

    boolean isReadWrite(Ref ref) {
        return cache.isReadWrite(ref)
    }

    boolean unlock(Ref ref) {
        return cache.unlock(ref)
    }

    boolean isReadOnly(Ref ref) {
        return cache.isReadOnly(ref)
    }

    String getOwner(Ref ref) {
        return cache.getOwner(ref)
    }

    Ref store(Referenceable referenceable) {
        doStore(referenceable)
        getRuleBaseSession().fireAllRules()
        return referenceable.reference
    }

    private void doStore(Referenceable referenceable) {
        referenceable.beforeStore(this)
        cache.putInCache(referenceable.getId(), referenceable)
        if (DEBUG) Logger.getLogger(this.class.name).debug("==> to application: ${referenceable.type} $referenceable")
        referenceable.afterStore()
        QueryManager.modifiedInApplication(referenceable)     // update query cache
        getRuleBaseSession().insert(referenceable)
    }


    boolean delete(Referenceable referenceable) {
        boolean deleted = false
        Ref ref = referenceable.reference
        if (!ref.isReadOnly()) { // delete not allowed if ref is readOnly
            List<Ref> family = referenceable.family() // ref + children + their children etc.
            // Attempt to grab locks on all children
            boolean allLocked
            synchronized (this) {
                allLocked = LockManager.lockAll(family)   // all locked or locks left as they were
            }
            // if fails, release any taken and abandon the delete
            // if succeeded, proceed to delete ref and children
            if (allLocked) {
                family.each {child ->
                    if (child as boolean) doDelete(child.deref())
                }
                deleted = true
            }
        }
        else {
            Logger.getLogger(this.class).warn("Attempted to delete readonly $ref")
        }
        if (deleted) getRuleBaseSession().fireAllRules()
        return deleted
    }

    private void doDelete(Referenceable referenceable) {
        Ref ref = referenceable.reference
        use(NoSessionCategory) {
            QueryManager.modifiedInApplication(referenceable)    // retrieve referenceable from application memory
        }
        getRuleBaseSession().retract(ref)
        cache.flushEntry(ref.id)     // unpersist
        referenceable.markDeleted() // raises change event on transient property 'delete'
        referenceable.afterDelete()
        ref.detach()
    }


    Referenceable retrieve(Ref ref) {
        Referenceable referenceable = null
        if (ref.isComputed() || ref.isInferred()) {
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
                Logger.getLogger(this.class.name).warn("Failed to retrieve $ref : $e")
            }
        }
        return (Referenceable) referenceable // will be cloned by SessionMemory
    }

    boolean isStored(Ref ref) {
        boolean stored = false
        use (NoSessionCategory) {
            stored = cache.isStored(ref)
        }
        return stored
    }

    boolean isFresh(Ref ref) {
        boolean fresh = false
        synchronized (this) {
            use(NoSessionCategory) { fresh = cache.isFresh(ref) }
        }
        return fresh
    }

    synchronized void clearAll() {
        cache.clear()
        ruleBaseSession = null
    }

    static Ref getRoot() {
        return new RefImpl(id: ROOT_ID, db: ROOT_DB)
    }

    // Creates a single YAML file named "<name>.yml" with ref and all that it references transitively
    int exportRef(Ref ref, String name) {
        String fileName = "${name}.yml"
        int count = 0
        String dir = getExportDirectory()
        File file = new File(dir, fileName)
        if (file.exists()) { // make a backup
            backupFile(fileName, file)
        }
        File tempFile = new File(dir, "_$fileName")
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
        File copy = new File(getBackupDirectory(), bkName)
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
        File file = new File(getExportDirectory(), "${name}.yml")
        if (!file.exists()) {
            Logger.getLogger(this.class.name).info("Import failed: unknown file $name")
        }
        else {
            FileInputStream input = new FileInputStream(file)
            YamlDecoder dec = new YamlDecoder(input)
            try {
                use(NoSessionCategory) {
                    while (true) {
                        Mappable a
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

    void fireAllRules() { // fire rules if needed
        getRuleBaseSession().fireAllRules()
    }

    boolean isFact(Ref ref) {
        return getRuleBaseSession().isFact(ref)
    }

    void insertFact(Ref ref) {
        use(NoSessionCategory) {
            if (!isFact(ref)) {
                List<Ref> queue = [ref]
                while (queue) {
                    Ref next = (Ref) queue.pop()
                    Referenceable referenceable = next.deref()
                    referenceable.references().each {aRef ->
                        if (!isFact(aRef)) queue.add(aRef)
                    }
                    getRuleBaseSession().insert(referenceable)
                }
                fireAllRules()
            }
        }
    }

    void sessionTimedOut(PlaybookSession session) {
        cache.sessionTimedOut(session)
    }

}