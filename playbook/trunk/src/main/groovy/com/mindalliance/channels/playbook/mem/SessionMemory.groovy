package com.mindalliance.channels.playbook.mem

import com.mindalliance.channels.playbook.ref.Store
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Reference
import com.mindalliance.channels.playbook.PlaybookApplication
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeEvent
import com.mindalliance.channels.playbook.ifm.IfmElement

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 10:06:30 AM
*/
class SessionMemory implements Store, PropertyChangeListener {

    Map<Reference, Referenceable> changes = new HashMap<Reference, Referenceable>()

    Referenceable retrieve(Reference reference) {
        Referenceable referenceable = changes.get(reference)
        if (!referenceable) {
            Referenceable appLevelReferenceable = retrieveFromApplicationMemory(reference)
            if (appLevelReferenceable) {
               referenceable =  (Referenceable)appLevelReferenceable.clone() // take a copy
                if (referenceable instanceof IfmElement) { // ...
                    ((IfmElement)referenceable).addPropertyChangeListener(this)
                }
            }
        }
        return referenceable
    }

    Reference persist(Referenceable referenceable) {    // Persist the change in session and, on save, in application
        Reference reference = referenceable.getReference()
        changes.put(reference, (Referenceable)referenceable)
        return reference
    }

    String getDefaultDb() {
        return ApplicationMemory.ROOT_DB;
    }

    void commit() {
        Collection<Referenceable> values = (Collection<Referenceable>)changes.values()
        getApplicationMemory().storeAll(values)
        resetChanges()
    }

    void abort() {
        resetChanges()
    }

    private void resetChanges() {
       changes = new HashMap<Reference, Referenceable>()
    }

    private Referenceable retrieveFromApplicationMemory(Reference reference) {
        return getApplicationMemory().retrieve(reference)
    }

    private ApplicationMemory getApplicationMemory() {
        return PlaybookApplication.get().memory
    }

    public void propertyChange(PropertyChangeEvent evt) {
       Referenceable referenceable = (Referenceable)evt.source
       persist(referenceable)  
    }

    public boolean isEmpty() {
        return changes.isEmpty()
    }

}