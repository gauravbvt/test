package com.mindalliance.channels.playbook.mem

import com.mindalliance.channels.playbook.ref.Store
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.PlaybookApplication
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeEvent
import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 10:06:30 AM
*/
class SessionMemory implements Store, PropertyChangeListener, Serializable {

    Map<Ref, Referenceable> changes = new HashMap<Ref, Referenceable>()
    Set<Ref> deletes = new HashSet<Ref>()

    Referenceable retrieve(Ref reference) {
        Referenceable referenceable = changes.get(reference)
        if (!referenceable) {
            Referenceable appLevelReferenceable = retrieveFromApplicationMemory(reference)
            if (appLevelReferenceable) {
               referenceable =  (Referenceable)appLevelReferenceable.copy() // take a copy
               referenceable.addPropertyChangeListener(this) // register with this session memory
            }
        }
        return referenceable
    }

    Ref persist(Referenceable referenceable) {    // Persist the change in session and, on save, in application
        Ref reference = referenceable.getReference()
        changes.put(reference, (Referenceable)referenceable)
        return reference
    }

    void forget(Ref ref) {
        changes.remove(ref)
        deletes.add(ref)
    }

    String getDefaultDb() {
        return ApplicationMemory.ROOT_DB;
    }

    void commit() {
        Collection<Referenceable> values = (Collection<Referenceable>)changes.values()
        getApplicationMemory().storeAll(values)
        getApplicationMemory().deleteAll(deletes)
        reset()
    }

    void abort() {
        reset()
    }

    private void reset() {
       changes = new HashMap<Ref, Referenceable>()
       deletes = new HashSet<Ref>()
    }

    private Referenceable retrieveFromApplicationMemory(Ref reference) {
        return getApplicationMemory().retrieve(reference)
    }

    private ApplicationMemory getApplicationMemory() {
        return PlaybookApplication.get().getMemory()
    }

    public void propertyChange(PropertyChangeEvent evt) {
       Referenceable referenceable = (Referenceable)evt.source
       persist(referenceable)
    }

    public boolean isEmpty() {
        return changes.isEmpty()
    }

    public int getSize() {
        int size = changes.size()
        return size
    }

}