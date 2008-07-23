package com.mindalliance.channels.playbook.ref
/**
 * Ref.deref() retrieves a Referenceable from session scope if there else from application scope.
 * Ref.begin() immediately puts a copy of Ref.deref() in session scope
 * Referenceable.persist() puts it in session scope and immediately marks it as changed (this is usually applied to a newly created Referenceable)
 * A Referenceable must be in session scope (it is keyed by its Ref) to be changed, except when changes are done within use (NoSessionCategory) {...}
 * A Ref must be in session to be deleted, except when within a NoSessionCategory
 */
interface Ref extends Serializable {
    String getId()
    void setId(String id)
    String getDb()
    void setDb(String db)
    Ref getReference()
    Referenceable deref()  // get Referenceable from session or from application (don't copy it nor register it to session)
    Object deref(String path)
    Object get(String name)
    Ref persist() // add modifiable copy to session change set
    void reset() // remove from session pending change or deletion
    void delete() // add to session deleted set
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
    boolean save() // commit self and all references (transitively) then export self and references to file named = id
    List<Ref> references()
    void begin() // returns a modifiable copy with current session as change listener. Noop if referenceable already in session.
    boolean isModifiable() // is this.deref() modifiable
    boolean isModified() // is in session and has been changed
    boolean isComputed()
    boolean isInferred()
    boolean isFresh() // is it not stale?
    void detach() // remove cached copy of referenceable
    boolean isAttached() // has a cached copy of referenceable
    boolean isReadOnly() // whether other seesion has a lock on this
    boolean lock() // return if lock acquired for the first time (RefLockException is raised if lock not acquired)
    boolean unlock() // unlock refs if locked by current session. Does nothing (and returns false) if not locked. Raises RefLockException is locked by other session.
    boolean isReadWrite() // whether this session hs a lock on this Ref
    String getOwner() // the name of the user who currently has a lock on the Ref
}
