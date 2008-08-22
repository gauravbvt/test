package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.query.Query
import org.apache.commons.collections.CollectionUtils
import com.mindalliance.channels.playbook.ifm.Jurisdictionable
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.ifm.Agent

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 11:21:56 AM
*/
class Position extends OrganizationResource implements Agent, Jurisdictionable {

    Location jurisdiction = new Location()
    List<Ref> roles = []

    void beforeStore(ApplicationMemory memory) {
        super.beforeStore(memory)
        if (jurisdiction.isDefined()) jurisdiction.detach()
    }

    boolean isAgent() {
        return true
    }

    boolean isLocatedWithin(Location loc) {
        return super.isLocatedWithin(loc) || jurisdiction.isWithin(loc)
    }

    boolean hasJurisdiction() {
        return jurisdiction.isDefined()
    }

    boolean hasRole(Ref role) {
        return (this.roles.any {resRole -> resRole.implies(role) })
    }


    // Queries

    List<Ref> findOtherPositionsInOrganization(List<Ref> positions) {
        List<Ref> otherPositions = new ArrayList<Ref>()
        if (organization != null) {
            List<Ref> allPositions = (List<Ref>)Query.execute(organization, "findAllPositions");
            otherPositions.addAll(CollectionUtils.subtract(allPositions, positions))
        }
        return otherPositions
    }

    List<Ref> findAllInPosition() {
        return (List<Ref>)project.jobs.findAll{job -> job.position == this.reference}
    }

    // End queries
    
}