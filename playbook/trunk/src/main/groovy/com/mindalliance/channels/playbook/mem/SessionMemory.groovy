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
import com.mindalliance.channels.playbook.query.QueryCache
import com.mindalliance.channels.playbook.query.QueryManager
import java.beans.PropertyChangeSupport
import com.mindalliance.channels.playbook.ifm.Channels

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2008
 * Time: 10:06:30 AM
 */
class SessionMemory implements Store, PropertyChangeListener, Serializable {

    // Refs under session
    Map<Ref, Referenceable> begun = new HashMap<Ref, Referenceable>() // Refs under session management. Locked as readWrite unless in ReadOnlyRefs.
    Set<Ref> readOnlyRefs = new HashSet<Ref>() // Refs begun while readOnly stay readonly until reset (even though other session may have released lock). This is needed to force a re-deref before becoming writable.

    // Ref changes and deletes to be persisted if committed
    Set<Ref> changes = new HashSet<Ref>()
    Set<Ref> deletes = new HashSet<Ref>()

    private QueryCache queryCache = new QueryCache()
    // any query execution dependent on elements belonging to any of these classes will be cached in session memory
    private Set<Class> inSessionClasses = new HashSet<Class>()
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this)


    void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener)
    }

    void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener)
    }

    private void propertyChanged(String name, def old, def value) {
        pcs.firePropertyChange(name, old, value)
    }

    private void changed(String name) {
        propertyChanged(name, null, this."$name")
    }

    QueryCache getQueryCache() {
        return queryCache
    }

    // return list of classes of elements in sessions
    Set<Class> inSessionClasses() {
        return inSessionClasses
    }

    Referenceable retrieve(Ref reference) {
        return retrieve(reference, null)
    }

    Referenceable retrieve(Ref reference, Referenceable dirtyRead) {
        Referenceable referenceable = null
        if (!deletes.contains(reference)) {  // deleted in session
            referenceable = begun.get(reference)
            if (!referenceable) {
                if (dirtyRead) {
                    return dirtyRead
                }
                else {
                    referenceable = retrieveFromApplicationMemory(reference)
                }
            }
            else {
                if (ApplicationMemory.DEBUG) Logger.getLogger(this.class.name).debug("<== from session: ${referenceable.type} $referenceable")
            }
        }
        return referenceable
    }

    void begin(Ref ref) {
        synchronized (this.getApplicationMemory()) {
            if (!begun.containsKey(ref)) {
                ref.detach()
                use(NoSessionCategory) {
                    Referenceable original = retrieveFromApplicationMemory(ref)
                    if (original) {
                        if (isReadOnly(ref)) {
                            readOnlyRefs.add(ref)
                            Logger.getLogger(this.class).warn("$ref is locked by another session; changes in this session will not be persisted.")
                            // throw new RefLockException("Can't begin: $ref is locked")
                        }
                        else {
                            ref.lock()
                        }
                        doBegin((Referenceable) original.copy())
                    }
                    else {
                        Logger.getLogger(this.class).warn("Can't begin on $ref: dereferences to null")
                    }
                }
            }
            else {
                Logger.getLogger(this.class).debug("Doing begin() on $ref already in session")
            }
        }
    }

    private void doBegin(Referenceable referenceable) {
        addChangeListeners(referenceable) // register with change listeners
        begun.put(referenceable.reference, referenceable)
        inSessionClasses.add(referenceable.class)
        changed("begun")
    }

    private void addChangeListeners(Referenceable referenceable) {
        referenceable.addPropertyChangeListener(this) // register with this session memory
        referenceable.addPropertyChangeListener(QueryManager.instance())
    }

    Ref persist(Referenceable referenceable) {  // must be newly created referenceable!
        // assert !referenceable.reference.isFresh()
        Ref ref = referenceable.reference
        doBegin(referenceable)
        referenceable.changed("id") // was created, thus has a new id
        // hasChanged(referenceable)
        return ref
    }

    Ref hasChanged(Referenceable referenceable) {// Persist the change in session and, on save, in application
        Ref reference = referenceable.getReference()
        assert begun.containsKey(reference)
        if (!readOnlyRefs.contains(referenceable.reference)) { // changes will not be persisted if ref entered session readOnly
            changes.add(reference)
            if (ApplicationMemory.DEBUG) Logger.getLogger(this.class.name).debug("==> to session: ${referenceable.type} $referenceable")
            changed("changes")
        }
        else {
            Logger.getLogger(this.class).warn("Readonly $ref was changed (changes will not be persisted)")
        }
        return reference
    }

    boolean delete(Ref ref) {
        if (!readOnlyRefs.contains(ref)) { // delete not allowed if ref entered session readOnly
            if (begun.containsKey(ref)) {
                ref.detach()
                ref.deref().changed("id") // raise change event on id
                begun.remove(ref)
                changes.remove(ref)
                deletes.add(ref)
                changed("deletes")
                changed("begun")
                changed("changes")
                return true
            }
            else {
                throw new NotModifiableException("Can't delete $ref : do begin() first")
            }
        }
        else {
            Logger.getLogger(this.class).warn("Attempted to delete readonly $ref")
        }
        return false
    }


    String getDefaultDb() {
        return ApplicationMemory.getDefaultDb();
    }

    void commit() {
        synchronized (getApplicationMemory()) {
            List<Referenceable> toCommit = []
            changes.each { toCommit.add(begun.get((Ref) it)) }
            getApplicationMemory().storeAll(toCommit)
            getApplicationMemory().deleteAll(deletes)
            reset()
            getApplicationMemory().exportRef(Channels.instance().reference, 'channels')    // TODO - temporary
        }
    }

    void abort() {
        synchronized (getApplicationMemory()) {
            reset()
        }
    }

    public void commit(Ref ref) {
        ref.detach()
        synchronized (getApplicationMemory()) {
            if (changes.contains(ref)) {
                Referenceable referenceable = begun.get(ref)
                changes.remove(ref)
                begun.remove(ref)
                getApplicationMemory().store(referenceable)
                ref.unlock()
            }
            else if (deletes.contains(ref)) {
                deletes.remove(ref)
                getApplicationMemory().delete(ref)
                ref.unlock()
            }
        }
        // does not update the query cache or inSessionClasses
    }


    // Remove Ref from session management. Release any lock held by session on Ref.
    // ref.begin() will be required to put ref under session management again
    void reset(Ref ref) {
        Logger.getLogger(this.class).info("Releasing $ref from session")
        ref.detach()
        synchronized (getApplicationMemory()) {
            if (begun.containsKey(ref)) {
                begun.remove(ref)
                readOnlyRefs.remove(ref)
                if (ref.isReadWrite()) ref.unlock()
            }
            if (changes.contains(ref)) {
                changes.remove(ref)   // no need to unlock again; was in begun
            }
            else if (deletes.contains(ref)) {
                deletes.remove(ref)
                if (ref.isReadWrite()) ref.unlock()
            }
        }
    }

    void reset() {
        synchronized (getApplicationMemory()) {
            changes = new HashSet<Ref>()
            unlockAll(deletes)
            deletes = new HashSet<Ref>()
            unlockAll(begun.keySet())
            begun = new HashMap<Ref, Referenceable>()
            queryCache.clear()
            inSessionClasses = new HashSet<Class>()
            readOnlyRefs = new HashSet<Ref>()
        }
    }

    private void unlockAll(Set<Ref> refs) {
        for (Ref ref: refs) {
            ref.detach()
            ref.unlock()
        }
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

    boolean save(Ref ref) {
        ref.references().each {it.commit()}
        ref.commit()
        int count = getApplicationMemory().exportRef(ref, ref.toString())
        return count > 0
    }

    boolean isModifiable(Ref ref) {
        return begun.containsKey(ref)
    }

    boolean isModified(Ref ref) {
        return changes.contains(ref)
    }

    boolean isFresh(Ref ref) {
        return isModifiable(ref) || getApplicationMemory().isFresh(ref)
    }

    boolean isReadOnly(Ref ref) {
        if (readOnlyRefs.contains(ref)) {
            return true
        }
        else {
            return PlaybookApplication.current().getAppMemory().isReadOnly(ref)
        }
    }
}