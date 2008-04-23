package com.mindalliance.channels.playbook.ifm.resources

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.resources.System
import com.mindalliance.channels.playbook.ifm.Locatable

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 11:16:29 AM
*/
class Resource extends IfmElement implements Locatable {

    String name = 'No name'
    String description = ''
    List<ContactInfo> contactInfos = []
    List<Ref> roles = []
    Location location
    List<Agreement> agreements = []
    List<Relationship> relationships  = []
    boolean effective = true // whether the resource is operational in real life

    String toString() { name }

    /**
     * Return subclass that a project user may want to create.
     */
    static List<Class<?>> contentClasses() {
        [
            Organization.class, Person.class, System.class,
            Position.class
        ]
    }

    @Override
    void beforeStore() {
        super.beforeStore()
        if (location) location.detach()
    }

    @Override
    void changed(String propName) {
        if (propName == 'location') {
            location.detach()
        }
        super.changed(propName)
    }

    public Location getLocation() {
        return location
    }
}