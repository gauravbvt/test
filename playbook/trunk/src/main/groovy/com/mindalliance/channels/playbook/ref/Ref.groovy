package com.mindalliance.channels.playbook.ref

interface Ref extends Serializable {
    String getId()
    String getDb()
    Referenceable getReferenced(Store store)
    Ref getReference()
    Referenceable deref()
    Object deref(String path)
    Object get(String name)
    Ref persist()
    void reset() // remove from session (pending change or delete)
    void delete()
    void commit() // commit only this Ref
    void become(Ref ref) // take the id and db of ref
    void changed(String propName) // the propName of the referenced Referenceable changed
    String getType() // return a short string identifying the type of the referenced
    Class formClass() // returns the form for editing this
    Ref find(String listPropName, Map<String, Object>args) // find the first element in named Ref list property that has all valued properties in args
    void add(Referenceable referenceable) // becomes add<Referenceable_type>(referenceable), e.g. add(aPosition) -> addPOsition(aPosition)
    void add(Ref ref) // ditto on ref.deref()
    void add(Referenceable referenceable, String type)  // use type parameter to compose add<Type>(referenceable)
    void add(Ref ref, String type)  // ditto on ref.deref()
    void remove(Referenceable referenceable) // becomes add<Referenceable_type>(referenceable), e.g. add(aPosition) -> addPOsition(aPosition)
    void remove(Ref ref) // ditto on ref.deref()
    void remove(Referenceable referenceable, String type)  // use type parameter to compose add<Type>(referenceable)
    void remove(Ref ref, String type)  // ditto on ref.deref()
    List<Ref> executeQuery(String query, Map<String,Object> args)
    boolean save() // commit self and all references (transitively) then export self and references to file named = id
    List<Ref> references()
}
