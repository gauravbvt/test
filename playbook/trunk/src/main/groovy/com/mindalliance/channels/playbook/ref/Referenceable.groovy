package com.mindalliance.channels.playbook.ref

import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty

interface Referenceable extends Bean {
    Ref getReference()
    void changed() // give the object a chance to clean up computed data
    void beforeStore()
    void afterStore()
    void afterRetrieve()
    Ref persist()
    void delete()
    void reset()
    Referenceable deref() // noop - returns self
    List<RefMetaProperty>metaProperties()
}
