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
class ReferenceImpl implements Reference, Serializable {

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

    public Reference getReference() {
        return this; // convenience method; returns self
    }

}