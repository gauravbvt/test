package com.mindalliance.channels.playbook.ref

interface Referenceable extends Bean {
    Ref getReference()
    void changed() // give the object a chance to clean up computed data
    void beforeStore()
    void afterStore()
    void afterRetrieve()
    Ref persist()
    void forget()
    Referenceable deref() // noop - returns self
}
