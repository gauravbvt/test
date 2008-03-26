package com.mindalliance.channels.playbook.ref

interface Store {
    Referenceable retrieve(Ref reference)
    Ref persist(Referenceable referenceable)
    void forget(Ref reference)
    String getDefaultDb()
}
