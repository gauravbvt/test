package com.mindalliance.channels.playbook.mem

import com.mindalliance.channels.playbook.ref.Store
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.PlaybookApplication
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeEvent
import org.apache.log4j.Logger
import com.mindalliance.channels.playbook.ref.impl.NotModifiableException
import com.mindalliance.channels.playbook.mem.NoSessionCategory

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2008
 * Time: 10:06:30 AM
 */
class SessionMemory implements Store, PropertyChangeListener, Serializable {

    Map<Ref, Referenceable> begun =  new HashMap<Ref, Referenceable>()
    Set<Ref> changes = new HashSet<Ref>()
    Set<Ref> deletes = new HashSet<Ref>()

    Referenceable retrieve(Ref reference) {
        Referenceable referenceable = null
        if (!deletes.contains(reference)) {  // deleted in session
            referenceable = begun.get(reference)
            if (!referenceable) {
                Referenceable appLevelReferenceable = retrieveFromApplicationMemory(reference)
        /*        if (appLevelReferenceable) {
                    referenceable = (Referenceable) appLevelReferenceable.copy() // take a copy
                    referenceable.addPropertyChangeListener(this) // register with this session memory
                }*/
                referenceable =  appLevelReferenceable
            }
            else {
                if (ApplicationMemory.DEBUG) Logger.getLogger(this.class.name).debug("<== from session: ${referenceable.type} $referenceable")
            }
        }
        return referenceable
    }

    void begin(Ref ref) {
        if (!begun.containsKey(ref)) {
            Referenceable referenceable
            use (NoSessionCategory) {
                Referenceable original = retrieveFromApplicationMemory(ref)
                if (original) {
                    referenceable = (Referenceable)original.copy() // take a copy
                    addChangeListeners(referenceable) // register with change listeners
                    begun.put(ref, referenceable)
                }
            }
        }
        else {
            Logger.getLogger(this.class).warn("Doing begin() on $ref already in session")
        }
    }

    private void addChangeListeners(Referenceable referenceable) {
        referenceable.addPropertyChangeListener(this) // register with this session memory
    }

    Ref persist(Referenceable referenceable) {
        Ref ref = referenceable.reference
        begun.put(ref, referenceable)
        hasChanged(referenceable)
        return ref
    }

    Ref hasChanged(Referenceable referenceable) {// Persist the change in session and, on save, in application
        Ref reference = referenceable.getReference()
        assert begun.containsKey(reference)
        changes.add(reference)
        if (ApplicationMemory.DEBUG) Logger.getLogger(this.class.name).debug("==> to session: ${referenceable.type} $referenceable")
        return reference
    }

    void delete(Ref ref) {
        if (begun.containsKey(ref)) {
            begun.remove(ref)
            changes.remove(ref)
            deletes.add(ref)
        }
        else {
            throw new NotModifiableException("Can't delete $ref : do begin() first")
        }
    }


    String getDefaultDb() {
        return ApplicationMemory.getDefaultDb();
    }

    void commit() {
        List<Referenceable> toCommit = []
        changes.each { toCommit.add(begun.get((Ref)it)) }
        getApplicationMemory().storeAll(toCommit)
        getApplicationMemory().deleteAll(deletes)
        reset()
    }

    void abort() {
        reset()
    }

    public void commit(Ref ref) {
        if (changes.contains(ref)) {
            Referenceable referenceable = begun.get(ref)
            changes.remove(ref)
            begun.remove(ref)
            getApplicationMemory().store(referenceable)
        }
        else if (deletes.contains(ref)) {
            deletes.remove(ref)
            getApplicationMemory().delete(ref)
        }
    }

    public void reset(Ref ref) {
        if (begun.containsKey(ref)) {
            begun.remove(ref)
        }
        if (changes.contains(ref)) {
            changes.remove(ref)
        }
        else if (deletes.contains(ref)) {
            deletes.remove(ref)
        }
    }

    private void reset() {
        changes = new HashSet<Ref>()
        deletes = new HashSet<Ref>()
        begun = new HashMap<Ref, Referenceable>()
    }

    Referenceable retrieveFromApplicationMemory(Ref reference) {
        Referenceable referenceable = getApplicationMemory().retrieve(reference)
        return referenceable
    }

    private ApplicationMemory getApplicationMemory() {
        return PlaybookApplication.current().getMemory()
    }

    void propertyChange(PropertyChangeEvent evt) {
        Referenceable referenceable = (Referenceable) evt.source
        hasChanged(referenceable)
    }

    boolean isEmpty() {  // no changes?
        return changes.isEmpty() && deletes.isEmpty()
    }

    int getSize() { // number of changes
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

    boolean isModifiable(Ref ref) {
        return begun.containsKey(ref)
    }

    public boolean isModified(Ref ref) {
        return changes.contains(ref)
    }
}