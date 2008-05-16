package com.mindalliance.channels.playbook.mem

import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.query.QueryManager

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 23, 2008
 * Time: 6:34:00 PM
 */
class NoSessionCategory {

    // REFERENCEABLE

    // Changes on out-of-session element allowed
    static void checkModifyingAllowed(ReferenceableImpl self) {
        // Do nothing       
    }

    // Don't raise change event
    static void doSetProperty(ReferenceableImpl self, String name, def value) {
        String setterName = "set${name[0].toUpperCase()}${name.substring(1)}"
        self."$setterName"(value)
        QueryManager.modified(self)
    }

    // Can't persist new elements
    static void persist(ReferenceableImpl self) {
        throw new Exception("Must be within session to persist")
    }

    // Dereference only from application memory
    static Referenceable retrieve(SessionMemory self, Ref reference) {
        Referenceable referenceable = self.retrieveFromApplicationMemory(reference)
        return referenceable
    }

    // REFERENCE

/*
    void doSetProperty(String name, def value) {
        this.@"$name" = value
    }

    static Referenceable dereference(RefImpl ref) {
        throw new Exception("Must be executed within a session")
    }
*/


}