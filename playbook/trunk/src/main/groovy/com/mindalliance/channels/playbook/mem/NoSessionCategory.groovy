package com.mindalliance.channels.playbook.mem

import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.impl.RefImpl

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 23, 2008
* Time: 6:34:00 PM
*/
class NoSessionCategory {

    // REFERENCEABLE

    static void checkModifyingAllowed(ReferenceableImpl self) {
        // Do nothing       
    }

   static void doSetProperty(ReferenceableImpl self, String name, def value) {
         String setterName = "set${name[0].toUpperCase()}${name.substring(1)}"
         self."$setterName"(value)
   }

    static void persist(ReferenceableImpl self) {
        throw new Exception("Must be within session to persist")
    }

    // REFERENCE

    void doSetProperty(String name, def value) {
        this.@"$name" = value
    }

    static Referenceable dereference(RefImpl ref) {
        throw new Exception("Must be executed within a session")
    }


}