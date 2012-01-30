package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.project.resources.Resource

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 5:28:48 PM
 */
class ResourceSpecification extends Specification {
    private static final long serialVersionUID = -1L;

    Class<? extends Bean> getMatchingDomainClass() {
        return Resource.class
    }

/*    List<Ref> getResourcesAt(Ref event) {
        if (!event as boolean) return []
        // Find all project-level resources that match this agent specification
        List<Ref> matchingResources = (List<Ref>)Query.execute(event.project, "findAllResourcesMatchingSpec", this, event)
        return matchingResources
    }*/
}