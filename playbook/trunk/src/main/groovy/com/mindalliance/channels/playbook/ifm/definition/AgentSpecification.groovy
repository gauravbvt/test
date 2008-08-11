package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 5:28:48 PM
 */
class AgentSpecification extends Specification {
    private static final long serialVersionUID = -1L;

    public Class<? extends Bean> getMatchingDomainClass() {
        return Agent.class
    }

    List<Ref> getResourcesAt(Ref event) {
        if (!event as boolean) return []
        // Find all project-level resources that match this agent specification
        List<Ref> matchingResources = Query.execute(event.project, "findAllAgentsMatchingSpec", this, event)
        return matchingResources
    }
}