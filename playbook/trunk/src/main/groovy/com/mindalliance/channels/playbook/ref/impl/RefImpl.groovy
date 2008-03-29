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

    public void commit() {
        Store store = PlaybookApplication.locateStore()
        store.commit(this)
    }

    public void changed(String propName) {
        deref().changed(propName)
    }

    public String getType() {
        return deref().getType()
    }

    public Class formClass() {
        return deref().formClass() //To change body of implemented methods use File | Settings | File Templates.
    }


}