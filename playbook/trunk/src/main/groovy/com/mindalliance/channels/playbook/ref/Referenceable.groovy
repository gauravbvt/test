package com.mindalliance.channels.playbook.ref

interface Referenceable extends java.io.Serializable {
    Reference getReference()
    Referenceable copy() 
}
