package com.mindalliance.channels.playbook.ref

interface Store {
    Referenceable retrieve(Ref reference)
    Ref persist(Referenceable referenceable)
    String getDefaultDb()
}
