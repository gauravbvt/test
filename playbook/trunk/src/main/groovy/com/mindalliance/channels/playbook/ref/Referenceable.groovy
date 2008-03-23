package com.mindalliance.channels.playbook.ref

interface Referenceable extends java.io.Serializable {
    Ref getReference()
    Referenceable copy()
    void changed() // give the object a chance to clean up computed data
    void beforeStore()
    void afterStore()
    void afterRetrieve()
}
