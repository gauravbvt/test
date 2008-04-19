package com.mindalliance.channels.playbook.ref

interface Store {
    Referenceable retrieve(Ref reference)
    Ref persist(Referenceable referenceable)
    void delete(Ref reference) // remove from session and from application if commit
    String getDefaultDb()
    boolean save(Ref ref)
}
