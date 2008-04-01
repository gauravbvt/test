package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Store
import com.mindalliance.channels.playbook.support.PlaybookApplication
import com.mindalliance.channels.playbook.support.RefUtils
import com.mindalliance.channels.playbook.support.RefUtils

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 8:47:26 AM
*/
class RefImpl implements Ref, GroovyInterceptable {

    String id
    String db

    // Two References are equal if they both have the same id (not null) and the same db (both can be null)
    boolean equals(Object obj) {
       if (!obj instanceof Ref) return false
       Ref ref = (Ref)obj
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

    public Referenceable getReferenced(Store store) {
        return store.retrieve(this)
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
             ref.@"$name" = value
         }
         else {
             Referenceable referenceable = deref()
             referenceable.setProperty(name, value)
         }
     }

    Referenceable deref() {
         if (this.@id == null) return null
         Store store = PlaybookApplication.locateStore()
         Referenceable referenceable = this.getReferenced(store)
         return referenceable
     }

    // Support for Java code that needs to dereference a Ref
    // Only supports dot-separated paths such as 'a.b.c' 
    def deref(String path) {
/*
        def result = this
        path.tokenize('.').each() {
            result = result."$it"
        }
*/      def result = RefUtils.get(this, path)
        return result
    }

    def get(String name) {
         if (['id', 'db'].contains(name)) {
             return ref.@"$name"
         }
         else {
             def value
             Referenceable referenceable
             try {
                 referenceable = deref()
                 if (referenceable) {
                    value = referenceable."$name"
                 }
                 else {
                     System.out.println("${this.toString()} is stale")  // TODO log this
                     throw new StaleRefException("${this.toString()} is stale")
                 }
             }
             catch (Exception e) {
                System.out.println("Can't get $name in $referenceable")  // TODO log this
                // throw new IllegalArgumentException("Can't get $name in $referenceable")
             }
             return value
         }
     }

    void delete() {
        Store store = PlaybookApplication.locateStore()
        store.delete(this)
    }

    void reset() {
        Store store = PlaybookApplication.locateStore()
        store.reset(this)
    }

    def invokeMethod(String name, def args) {
        def metamethod = this.class.metaClass.getMetaMethod(name, args)
        if (metamethod == null) {// don't override a defined method
            def value
            Referenceable referenceable = deref()
            value = referenceable.invokeMethod(name, args)
            return value
        }
        if (metamethod == null) {  // TODO - ????
            throw new Exception("No method named $name")
        }
        return metamethod.invoke(this, args)
    }

    void become(Ref ref) {
        this.id = ref.id
        this.db = ref.db
    }

    void commit() {
        Store store = PlaybookApplication.locateStore()
        store.commit(this)
    }

    void changed(String propName) {
        deref().changed(propName)
    }

    String getType() {
        return deref().getType()
    }

    Class formClass() {
        return deref().formClass() //To change body of implemented meth, ods use File | Settings | File Templates.
    }

    List drillDown(String listPropName, List drillDownPropNames, Map<String, Object>ddValues) {
        return deref().drillDown(listPropName, drillDownPropNames, ddValues)
    }

    Ref find(String listPropName, Map<String, Object>args) {
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
        this."add$suffix"(referenceable)
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
        referenceable.persist() // make sure it is persisted TODO -- first test that it is *not* in the app memory
        String suffix = RefUtils.capitalize(type)
        this."remove$suffix"(referenceable)
    }


}