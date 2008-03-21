package com.mindalliance.channels.playbook.ref

interface Store {
    Referenceable retrieve(Reference reference)
    Reference persist(Referenceable referenceable)
    String getDefaultDb()
}
