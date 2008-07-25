package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Store
import com.mindalliance.channels.playbook.support.PlaybookApplication
import com.mindalliance.channels.playbook.support.RefUtils
import org.apache.log4j.Logger
import com.mindalliance.channels.playbook.mem.LockManager

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2008
 * Time: 8:47:26 AM
 */
class RefImpl implements Ref {

    String id
    String db
    Referenceable value // set once on deref() by store. Transient and detachable

    RefImpl() {}

    RefImpl(String id) {
        this.id = id
        if (id == null) {
            Logger.getLogger(this.class).warn("Instantiating with null id")
        }
    }

    void detach() {
        value = null
    }

    boolean isComputed() {
        return false
    }

    boolean isInferred() {
        return false
    }

    boolean isAttached() {
        return value != null
    }

    void attach(Referenceable referenceable) {
        value = referenceable
    }

    // Two References are equal if they both have the same id (not null) and the same db (both can be null)
    boolean equals(Object obj) {
        if (obj == null || !obj instanceof Ref) return false
        Ref ref = (Ref) obj
        if (ref.id == null || this.id == null || this.id != ref.id) return false // both ids must be set for equality
        if (ref.db != this.db) return false  // both dbs must be the same and can both be null
        return true
    }

    int hashCode() {
        int hash = 1
        if (this.id) hash = hash * 31 + this.id.hashCode()
        if (this.db) hash = hash * 31 + this.db.hashCode()
        return hash
    }

    String toString() {
        return "Ref<$id,$db>"
    }

    Referenceable deref() {   // returns Referenceable from session change set or UNCOPIED referenceable from application
        if (this.@id == null) return null
        Store store = PlaybookApplication.locateStore()
        Referenceable referenceable = this.getReferenced(store)
        return referenceable
    }

    // Support for Java code that needs to dereference a Ref
    // Only supports dot-separated paths such as 'a.b.c'
    def deref(String path) {
        def result = RefUtils.get(this, path)
        return result
    }

    private Referenceable getReferenced(Store store) {
        Referenceable referenceable = store.retrieve(this, value)
        value = referenceable
        return referenceable
    }

    String getDb() {
        return db
    }

    String getId() {
        return id
    }

    void setId(String val) {
        if (id != null) {
            throw new Exception("Not allowed to change the id of a Ref once set")
        }
        else {
            if (val == null) {
                Logger.getLogger(this.class).warn("Setting a ref with null id");
            }
            id = val
        }
    }

    void setDb(String val) {
        if (db != null) {
            throw new Exception("Not allowed to change the db of a Ref once set")
        }
        else {
            db = val
        }
    }

    Ref getReference() {
        return this; // convenience method; returns self
    }

    void setProperty(String name, def value) {
        doSetProperty(name, value)
    }

    void doSetProperty(String name, def value) {
        if (['id', 'db'].contains(name)) {
            this.@"$name" = value
        }
        else {
            Referenceable referenceable = deref()
            referenceable.setProperty(name, value)
        }
    }

    def get(String name) {
        if (['id', 'db'].contains(name)) {
            return this.@"$name"
        }
        else {
            def value = null
            Referenceable referenceable = null
            try {
                referenceable = deref()
                if (referenceable) {
                    value = referenceable."$name"
                }
                else {
                    Logger.getLogger(this.getClass().getName()).warn("${this.toString()} is stale")
                    throw new StaleRefException("${this.toString()} is stale")
                }
            }
            catch (Exception e) {
                Logger.getLogger(this.class.name).warn("Can't get $name in $referenceable")
                // throw new IllegalArgumentException("Can't get $name in $referenceable")
            }
            return value
        }
    }

    boolean delete() {
        boolean deleted = false
        Referenceable referenceable = this.deref()
        if (referenceable != null) {
            // cascaded delete in application scope, or noop if can not be completely done
            deleted = PlaybookApplication.current().appMemory.delete(referenceable)
        }
        detach()
        return deleted
    }

    Ref persist() {
        this.deref().persist()
    }

    void reset() {
        Store store = PlaybookApplication.locateStore()
        store.reset(this)
        detach()
    }

    // Forward undefined methods to referenceable
    def invokeMethod(String name, def args) {
        def value
        Referenceable referenceable = deref()
        def metamethod = referenceable.class.metaClass.getMetaMethod(name, args)
        if (metamethod == null) {
            value = referenceable.invokeMethod(name, args)  // call referenceable's undefined method handler
        }
        else {
            value = referenceable.metaClass.invokeMethod(referenceable, name, args)  // invoke referenceable's defined method
        }
        return value
    }

    void become(Ref ref) {
        this.id = ref.id
        this.db = ref.db
    }

    void commit() {
        Store store = PlaybookApplication.locateStore()
        store.commit(this)
    }

    boolean isModifiable() {
        Store store = PlaybookApplication.locateStore()
        store.isModifiable(this)
    }

    void changed(String propName) {
        deref().changed(propName)
    }

    String getType() {
        return deref().getType()
    }

    Class formClass() {
        return deref().formClass()
    }

    Ref find(String listPropName, Map<String, Object> args) {
        return deref().find(listPropName, args)
    }

    void add(Ref ref) {
        this.add(ref.deref())
    }

    void add(Referenceable referenceable) {
        String type = referenceable.type
        this.add(referenceable, type)
    }

    void add(Ref ref, String type) {
        this.add(ref.deref(), type)
    }

    void add(Referenceable referenceable, String type) {
        referenceable.persist() // make sure it is persisted TODO -- first test that it is *not* in the app memory
        String suffix = RefUtils.capitalize(type)
        this."add$suffix"(referenceable.reference)
    }

    void remove(Ref ref) {
        this.remove(ref.deref())
    }

    void remove(Referenceable referenceable) {
        String type = referenceable.type
        this.remove(referenceable, type)
    }

    void remove(Ref ref, String type) {
        this.remove(ref.deref(), type)
    }

    void remove(Referenceable referenceable, String type) {
        // referenceable.persist() // make sure it is persisted TODO -- first test that it is *not* in the app memory
        String suffix = RefUtils.capitalize(type)
        this."remove$suffix"(referenceable.reference)
    }

    List<Ref> references() {
        return this.deref().references()
    }

    // commit and export to file this and its graph of references. Return whether successful.
    boolean save() {
        Store store = PlaybookApplication.locateStore()
        return store.save(this)
    }

    // Returns a modifiable copy with current session as change listener.  Noop if referenceable already in session.
    void begin() {
        if (this.@id != null) {
            Store store = PlaybookApplication.locateStore()
            store.begin(this)
        }
    }

    boolean isModified() {
        Store store = PlaybookApplication.locateStore()
        return store.isModified(this)
    }

    Object asType(Class type) {
        if (type == Boolean.class || type == boolean) {
            return isFresh()
        }
        else {
            return super.asType(type)
        }
    }

    boolean isFresh() {
        if (this.@id == null) return false
        if (isAttached()) return true
        Store store = PlaybookApplication.locateStore()
        boolean fresh = store.isFresh(this)
        // boolean fresh = (deref() != null)   // by testing with deref, the Ref caches any referenceable, and freshness is sticky
        if (!fresh) {
            this.@id = null // nullify the id
        }
        return fresh
    }

    String getOwner() {
        return LockManager.getOwner(this)
    }

    boolean lock() {
        return LockManager.lock(this)
    }

    boolean unlock() {
        return LockManager.unlock(this)
    }

    boolean isReadWrite() {
        return LockManager.isReadWrite(this)
    }

    boolean isReadOnly() {
        Store store = PlaybookApplication.locateStore()
        return store.isReadOnly(this)   // this session has ref as readOnly or some other sessions has a lock on the Ref
    }


}