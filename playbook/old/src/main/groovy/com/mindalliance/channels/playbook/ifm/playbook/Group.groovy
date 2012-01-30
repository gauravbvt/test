package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.project.resources.ContactInfo
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Responsibility
import com.mindalliance.channels.playbook.ifm.Jurisdictionable

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
    AgentSpecification agentSpec = new AgentSpecification()  // all matching agents
    List<ContactInfo> contactInfos

    String toString() {
        return name
    }

    boolean hasJurisdiction() {
        return false;
    }

    boolean hasLocation() {
        return false;
    }

    boolean isAnOrganization() {
        return false;
    }

    boolean isAnIndividual() {
        return false;
    }

    Location getLocation() {
        return new Location();   // undefined
    }

    // queries

     // end queries

    List<Ref> getRoles() {
        return agentSpec.roles;
    }

    List<Responsibility> getResponsibilities() {
        return roles.responsibilities.flatten();
    }

    boolean hasRole(Ref role) {
        return roles.any {r -> r.implies(role)};
    }

}