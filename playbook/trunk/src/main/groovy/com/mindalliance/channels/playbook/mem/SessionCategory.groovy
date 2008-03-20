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

    /*
        def invokeMethod(String name, def args) {
           switch (name) {
               case ~/^set/: // Set the field and raise change event
                             String fieldName = name.substring(3).toLowerCase()
                             set(fieldName, args)
                             break
               case ~/^add/: // later
               case ~/^remove/:
               default: // Run the method as is
                        def metamethod = this.class.metaClass.getMetaMethod(name, args)
                        return metamethod.invoke(this, args)
           }
        }
    */


    static void doSetProperty(ReferenceableImpl self, String name, def value) {
        def old = self."$name"
        def newValue
        if (value instanceof Referenceable) {
            newValue = ((Referenceable) value).getReference()
        }
        else {
            newValue = value
        }
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

    static Object get(ReferenceImpl ref, String name) {
         if (['id', 'db'].contains(name)) {
             return ref.@"$name"
         }
         else {
             def value
             Referenceable referenceable = dereference(ref)
             value = referenceable."$name"
             return value
         }
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

     static Object invokeMethod(ReferenceImpl ref, String name, Object args) {
         Object value
         Referenceable referenceable = dereference(ref)
         value = referenceable.invokeMethod(name, args)
         return value

     }

    // support

    private static Store locateStore() {  // TODO move to PlaybookSession
        PlaybookSession session = (PlaybookSession) Session.get()
        return (Store) session.memory
    }




}