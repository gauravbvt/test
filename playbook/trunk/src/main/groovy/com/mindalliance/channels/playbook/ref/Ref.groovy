package com.mindalliance.channels.playbook.ref

interface Ref extends Serializable {
    String getId()
    String getDb()
    Referenceable getReferenced(Store store)
    Ref getReference()
}
