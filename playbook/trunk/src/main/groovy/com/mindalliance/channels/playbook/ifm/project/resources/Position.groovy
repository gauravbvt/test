package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.query.Query
import org.apache.commons.collections.CollectionUtils

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 11:21:56 AM
*/
class Position extends OrganizationResource {

    Location jurisdiction
    List<Ref> managedPositions = []

    void beforeStore() {
        super.beforeStore()
        if (jurisdiction) jurisdiction.detach()
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
    
}