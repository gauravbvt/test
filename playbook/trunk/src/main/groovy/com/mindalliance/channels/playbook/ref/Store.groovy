package com.mindalliance.channels.playbook.ref

interface Store {

    Referenceable retrieve(Ref reference, Referenceable dirtyRead)
    Ref persist(Referenceable referenceable)  // marks referenceable's ref as both begun and changed
    void commit()
    void commit(Ref reference)
    boolean delete(Ref reference) // remove from session and from application if commit
    String getDefaultDb()
    boolean save(Ref ref)
    void begin(Ref ref)
    boolean isModifiable(Ref ref)
    boolean isModified(Ref ref)
    boolean isFresh(Ref ref)  // i.e. not stale -- calling deref() will return a Referenceable
    boolean isReadOnly(Ref ref) // in sessin as readOnly or other session has lock on ref
    void reset()
    void reset(Ref ref)
}
