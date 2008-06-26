package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ifm.Jurisdictionable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.playbook.Event

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 20, 2008
 * Time: 4:52:48 PM
 */
class Team extends Resource implements Jurisdictionable {

    List<Ref> resources = []  // Identified and invariant
    Location jurisdiction = new Location()

    String toString() {
        return name
    }

    public List<Ref> getResourcesAt(Event event) {
        return resources;
    }

    public boolean isResourceElement() {
        return false;
    }

    public boolean isTeam() {
        return true;
    }

    public boolean isGroup() {
        return false;
    }


}