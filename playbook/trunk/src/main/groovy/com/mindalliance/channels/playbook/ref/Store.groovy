package com.mindalliance.channels.playbook.ref

interface Store {

    Referenceable retrieve(Ref reference, Referenceable dirtyRead)
    Ref persist(Referenceable referenceable)  // marks referenceable's ref as both begun and changed
    void commit()
    void commit(Ref reference)
    void delete(Ref reference) // remove from session and from application if commit
    String getDefaultDb()
    boolean save(Ref ref)
    void begin(Ref ref)
    boolean isModifiable(Ref ref)
    boolean isModified(Ref ref)
    boolean isFresh(Ref ref)  // i.e. not stale -- calling deref() will return a Referenceable
}
