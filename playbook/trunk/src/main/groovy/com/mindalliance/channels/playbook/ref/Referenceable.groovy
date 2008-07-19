package com.mindalliance.channels.playbook.ref

import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty
import com.mindalliance.channels.playbook.Identified
import com.mindalliance.channels.playbook.mem.ApplicationMemory

interface Referenceable extends Bean, Identified {

    String getDb()
    Ref getReference()
    void changed() // give the object a chance to clean up computed data
    void changed(String propName) // signals that the value of propName has changed
    void beforeStore(ApplicationMemory memory)
    void afterStore()
    void afterRetrieve()
    Ref persist()
    void delete()
    void commit() // commit only this
    void reset() // remove pending change or delete from session
    Referenceable deref() // noop - returns self
    List<RefMetaProperty>metaProperties()
    String getType() // return a short string identifying the type of the referenceable
    Class formClass() // the class of the form to edit this
    Ref find(String listPropName, Map<String, Object>args) // find the first element in named Ref list property that has all valued properties in args
    List<Ref> references() // find all references in this
    boolean save() // commit self and all references(tranisitively) then export self and references to file
    void afterDelete()
    void makeConstant()
    boolean isConstant()
    Set hiddenProperties() // non-list, "internal use" properties, i.e. not to be displayed to end-user as element discriminator
}
