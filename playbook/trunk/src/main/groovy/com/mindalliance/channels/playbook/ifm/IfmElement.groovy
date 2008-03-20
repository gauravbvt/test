package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl
import com.mindalliance.channels.playbook.ref.Reference
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import com.mindalliance.channels.playbook.mem.ApplicationMemory
/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 12:36:45 PM
*/
abstract class IfmElement extends ReferenceableImpl {

    PropertyChangeSupport pcs = new PropertyChangeSupport(this)

    void addPropertyChangeListener( PropertyChangeListener listener ) {
        this.pcs.addPropertyChangeListener( listener )
    }

    void removePropertyChangeListener( PropertyChangeListener listener ) {
        this.pcs.removePropertyChangeListener( listener )
    }

    Object clone() {
        IfmElement clone = (IfmElement)super.clone()
        clone.pcs = new PropertyChangeSupport(clone)
        return clone
    }


    void changed(String propName) { // MUST be called when ifmElement is changed other than via a property get/set
        propertyChanged(propName, null, this.@"$propName") // don't care about old value
    }

    void propertyChanged(String name, def old, def value) {
       pcs.firePropertyChange(name, old, value)
    }

    void makeRoot() {
        Reference root = ApplicationMemory.ROOT
        this.id = root.id
        this.db = root.db
    }

}