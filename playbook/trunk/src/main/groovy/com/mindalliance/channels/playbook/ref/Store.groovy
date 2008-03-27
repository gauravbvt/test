package com.mindalliance.channels.playbook.ref

interface Store {
    Referenceable retrieve(Ref reference)
    Ref persist(Referenceable referenceable)
    void delete(Ref reference) // remove from session and from application if commit
    void reset(Ref reference)  // remove form session only
    String getDefaultDb()
}
