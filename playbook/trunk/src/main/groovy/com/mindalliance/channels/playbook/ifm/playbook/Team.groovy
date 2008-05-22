package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Agent

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 9:45:49 PM
 */
class Team extends PlaybookElement implements Agent {

    String name = ''
    String description = ''
    List<Ref> resources = []  // Identified and invariant

    public List<Ref> getResourcesAt(Event event) {
        return resources;
    }

    public boolean isResource() {
        return false;
    }

    public boolean isTeam() {
        return true;
    }

    public boolean isGroup() {
        return false;
    }
}