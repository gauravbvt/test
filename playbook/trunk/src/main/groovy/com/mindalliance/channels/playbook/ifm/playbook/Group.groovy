package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.Responsibility
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 9:46:51 PM
 */
class Group extends PlaybookElement implements Agent {

    String name = ''
    String description = ''
    AgentSpecification agentSpec = new AgentSpecification()

    @Override
    List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['resourceKinds', 'responsibilities', 'location', 'group',
                                                              'resourceElement'])
    }

    String toString() {
        return name
    }

    boolean isResourceElement() {
         return false
     }

    boolean isGroup() {
         return true
     }

    boolean isAnOrganization() {
        return false
    }

    boolean hasJurisdiction() {
        return false
    }

    boolean hasLocation() {
        return false
    }

    List<Responsibility> getResponsibilities() {    // a group as a whole has no responsibility? -- TODO
        return []
    }

    Location getLocation() {   // a group has no defined location? TODO --
        return new Location()
    }

    // queries
    List<Ref> getResourcesAt(Ref event) {
        if (event as boolean)
            return agentSpec.getResourcesAt(event)
        else
            return []
     }
     // end queries


 }