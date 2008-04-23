package com.mindalliance.channels.playbook.ref

interface Store {

    Referenceable retrieve(Ref reference)
    Ref persist(Referenceable referenceable)  // marks referenceable's ref as both begun and changed
    void delete(Ref reference) // remove from session and from application if commit
    String getDefaultDb()
    boolean save(Ref ref)
    void begin(Ref ref)
    boolean isModifiable(Ref ref)
}
