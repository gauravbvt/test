package com.mindalliance.channels.playbook.ref

interface Ref extends Serializable {
    String getId()
    String getDb()
    Referenceable getReferenced(Store store)
    Ref getReference()
    Referenceable deref()
    Object deref(String path)
    Object get(String name)
    void reset() // remove from session (pending change or delete)
    void delete()
    void commit() // commit only this Ref
    void become(Ref ref) // take the id and db of ref
    void changed(String propName) // the propName of the referenced Referenceable changed
    String getType() // return a short string identifying the type of the referenced
}
