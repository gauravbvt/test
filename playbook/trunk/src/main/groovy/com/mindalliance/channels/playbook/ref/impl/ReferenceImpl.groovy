package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Reference
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Store

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 8:47:26 AM
*/
class ReferenceImpl implements Reference, Serializable, GroovyInterceptable {

    String id
    String db

    // Two References are equal if they both have the same id (not null) and the same db (both can be null)
    boolean equals(Object obj) {
       if (!obj instanceof Reference) return false
       Reference ref = (Reference)obj
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
            throw new Exception("Not allowed to change the id of a Reference once set")
        }
        else {
           id = val
        }
    }

    void setDb(String val) {
        if (db != null) {
            throw new Exception("Not allowed to change the db of a Reference once set")
        }
        else {
           db = val
        }
    }

    Reference getReference() {
        return this; // convenience method; returns self
    }

    void setProperty(String name, def value) {
        doSetProperty(name, value)
    }

    void doSetProperty(String name, def value) {
        this.@"$name" = value
    }

    def get(String name) {
         if (['id', 'db'].contains(name)) {
             return ref.@"$name"
         }
         else {
             def value
             Referenceable referenceable = dereference()
             value = referenceable."$name"
             return value
         }
     }



    def invokeMethod(String name, Object args) {
        def metamethod = this.class.metaClass.getMetaMethod(name, args)
        if (metamethod == null) {// don't override a defined method
            def value
            Referenceable referenceable = dereference()
            value = referenceable.invokeMethod(name, args)
            return value
        }
        if (metamethod == null) {
            throw new Exception("No method named $name")
        }
        return metamethod.invoke(this, args)
    }

    Referenceable dereference() {
        throw new Exception("Must be executed within a session")
    }



}