package com.mindalliance.channels.playbook.ref

interface Reference extends Serializable {
    String getId()
    String getDb()
    Referenceable getReferenced(Store store)
    Reference getReference()
}
