package com.mindalliance.channels.playbook.mem

import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Store
import com.mindalliance.channels.playbook.PlaybookSession
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl
import com.mindalliance.channels.playbook.ref.impl.ReferenceImpl

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 20, 2008
* Time: 1:21:07 PM
*/
class SessionCategory {

    // REFERENCEABLE

    static void doSetProperty(ReferenceableImpl self, String name, def value) {
        def old = self."$name"
        String setterName = "set${name[0].toUpperCase()}${name.substring(1)}"
        self."$setterName"(value)
        if (!['id', 'db', 'pcs'].contains(name)) {
            self.propertyChanged(name, old, value)
        }
    }

    static void persist(ReferenceableImpl referenceable) {
        Store store = locateStore()
        store.persist(referenceable)
    }

    // REFERENCE

    static Referenceable dereference(ReferenceImpl ref) {
         if (ref.@id == null) return null
         Store store = locateStore()
         Referenceable referenceable = ref.getReferenced(store)
         return referenceable
     }

    static void doSetProperty(ReferenceImpl ref, String name, def value) {
         if (['id', 'db'].contains(name)) {
             ref.@"$name" = value
         }
         else {
             Referenceable referenceable = dereference(ref)
             referenceable.setProperty(name, value)
         }
     }

    // support

    private static Store locateStore() {  // TODO move to PlaybookSession
        PlaybookSession session = (PlaybookSession) Session.get()
        return (Store) session.memory
    }




}