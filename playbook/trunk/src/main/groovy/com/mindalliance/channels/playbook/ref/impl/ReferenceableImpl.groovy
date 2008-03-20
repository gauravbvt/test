package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Reference
import java.beans.PropertyChangeSupport
import java.beans.PropertyChangeListener
import com.mindalliance.channels.playbook.ref.Store

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 8:49:28 AM
*/
abstract class ReferenceableImpl implements Referenceable, GroovyInterceptable {

    String id
    String db

    PropertyChangeSupport pcs = new PropertyChangeSupport(this)

    void addPropertyChangeListener( PropertyChangeListener listener ) {
        this.pcs.addPropertyChangeListener( listener )
    }

    void removePropertyChangeListener( PropertyChangeListener listener ) {
        this.pcs.removePropertyChangeListener( listener )
    }

    Referenceable copy() {
        Referenceable copy = (Referenceable)this.class.newInstance()
        copy.id = this.@id
        copy.db = this.@db
        copy.pcs = new PropertyChangeSupport(copy)
        getProperties().each {name,val ->
            if (!['id','db','pcs','class','reference','metaClass'].contains(name)) {
                copy."$name" = val
            }
        }
        return copy
    }

    void changed(String propName) { // MUST be called when ifmElement is changed other than via a property get/set
        propertyChanged(propName, null, this.@"$propName") // don't care about old value
    }

    void propertyChanged(String name, def old, def value) {
       pcs.firePropertyChange(name, old, value)
    }

    String getId() {
        return id ?: (id = makeGuid())    // If no id is given, make one
    }

    Reference getReference() {
        return new ReferenceImpl(id: getId(), db: getDb())
    }

    private String makeGuid() {
        String uuid = "${UUID.randomUUID()}"
        return uuid
    }

    void setProperty(String name, def value) {
        doSetProperty(name, value)
    }

    void doSetProperty(String name, def value) {
        String setterName = "set${name[0].toUpperCase()}${name.substring(1)}"
        this."$setterName"(value)
    }


}