package com.mindalliance.channels.playbook.mem

import com.mindalliance.channels.playbook.ref.Store
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.PlaybookApplication
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeEvent
import org.apache.log4j.Logger


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
        Referenceable referenceable = null
        if (!deletes.contains(reference)) {  // deleted in session
            referenceable = changes.get(reference)
            if (!referenceable) {
                Referenceable appLevelReferenceable = retrieveFromApplicationMemory(reference)
                if (appLevelReferenceable) {
                    referenceable = (Referenceable) appLevelReferenceable.copy() // take a copy
                    referenceable.addPropertyChangeListener(this) // register with this session memory
                }
            }
            else {
                if (ApplicationMemory.DEBUG) Logger.getLogger(this.class.name).debug("<== from session: ${referenceable.type} $referenceable")
            }
        }
        return referenceable
    }

    Ref persist(Referenceable referenceable) {// Persist the change in session and, on save, in application
        Ref reference = referenceable.getReference()
        changes.put(reference, (Referenceable) referenceable)
        if (ApplicationMemory.DEBUG) Logger.getLogger(this.class.name).debug("==> to session: ${referenceable.type} $referenceable")
        return reference
    }

    void delete(Ref ref) {
        changes.remove(ref)
        deletes.add(ref)
    }


    String getDefaultDb() {
        return ApplicationMemory.getDefaultDb();
    }

    void commit() {
        Collection<Referenceable> values = (Collection<Referenceable>) changes.values()
        getApplicationMemory().storeAll(values)
        getApplicationMemory().deleteAll(deletes)
        reset()
    }

    void abort() {
        reset()
    }

    public void commit(Ref ref) {
        if (changes.containsKey(ref)) {
            Referenceable referenceable = changes.get(ref)
            changes.remove(ref)
            getApplicationMemory().store(referenceable)
        }
        else if (deletes.contains(ref)) {
            deletes.remove(ref)
            getApplicationMemory().delete(ref)
        }
    }

    public void reset(Ref ref) {
        if (changes.containsKey(ref)) {
            changes.remove(ref)
        }
        else if (deletes.contains(ref)) {
            deletes.remove(ref)
        }
    }

    private void reset() {
        changes = new HashMap<Ref, Referenceable>()
        deletes = new HashSet<Ref>()
    }

    private Referenceable retrieveFromApplicationMemory(Ref reference) {
        Referenceable referenceable = getApplicationMemory().retrieve(reference)
        return referenceable
    }

    private ApplicationMemory getApplicationMemory() {
        return PlaybookApplication.get().getMemory()
    }

    void propertyChange(PropertyChangeEvent evt) {
        Referenceable referenceable = (Referenceable) evt.source
        persist(referenceable)
    }

    boolean isEmpty() {
        return changes.isEmpty() && deletes.isEmpty()
    }

    int getSize() {
        int size = changes.size()
        return size
    }

    int getPendingDeletesCount() {
        return deletes.size()
    }

    public boolean save(Ref ref) {
        ref.references().each {it.commit()}
        this.commit()
        int count = getApplicationMemory().exportRef(ref, ref.toString())
        return count > 0
    }
}