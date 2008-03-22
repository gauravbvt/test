package com.mindalliance.channels.playbook.ref

interface Referenceable extends java.io.Serializable {
    Ref getReference()
    Referenceable copy() 
}
