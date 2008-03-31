package com.mindalliance.channels.playbook.ifm.context.environment

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Location

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 12:10:42 PM
*/
class Organization extends Resource {

    Location address = new Location() // not a Ref because not an independent element (is a component of the Organization)
    Ref parent
    // List<Ref> positions = []
    List<Ref> systems = []
    Location jurisdiction = new Location()
    List<Ref> organizationTypes = []

    void beforeStore() {
        address.detach()
        jurisdiction.detach()
    }

    List<Ref>findPositions() {
        currentProject().resources.findAll {res ->
            res.type == 'Position' && res.organization == this.reference
        }
    }

    Ref findPositionNamed(String name) {
        return (Ref)findPositions().find {position -> position.name.equalsIgnoreCase(name)}
    }



}