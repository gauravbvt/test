package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Reference
import com.mindalliance.channels.playbook.ref.Store
import com.mindalliance.channels.playbook.PlaybookSession
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.ifm.IfmElement

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 9:13:03 AM
*/
class ReferenceCategory {

    // REFERENCE

    static private Store locateStore() {
        PlaybookSession session = (PlaybookSession)Session.get()
        return (Store)session.memory
    }

    static Referenceable dereference(Reference ref) {
        if (ref.id == null) return null
        Store store = locateStore()
        Referenceable referenceable = ref.getReferenced(store)
        return referenceable
    }

    static Object get(Reference ref, String name) {
        if (['id', 'db', 'referenced', 'context'].contains(name)) {
            return ref.@"$name"
        }
        else {
            def value
            Referenceable referenceable = dereference(ref)
            value = referenceable."$name"
            return value
        }
    }

    static void set(Reference ref, String name, def value) {
        if (['id', 'db'].contains(name)) {
            ref.@"$name" = value
        }
        else {
            Referenceable referenceable = dereference(ref)
            use (ReferenceCategory) {
                referenceable.set(name,value)
            }
        }
    }

    static Object invokeMethod(Reference ref, String name, Object args) {
        Object value
        Referenceable referenceable = dereference(ref)
        value = referenceable.invokeMethod(name, args)
        return value

    }

    // REFERENCEABLE

    static void persist(Referenceable referenceable) {
        Store store = locateStore()
        store.persist(referenceable)
    }

    // IFMELEMENT

    static void set(IfmElement el, String name, def value) {
        def old = el.@"$name"
        el.@"$name" = value
        el.propertyChanged(name, old, value)
    }
}