package com.mindalliance.channels.playbook.ref

interface Reference {
    String getId()
    String getDb()
    Referenceable getReferenced(Store store)
    Reference getReference()
}
