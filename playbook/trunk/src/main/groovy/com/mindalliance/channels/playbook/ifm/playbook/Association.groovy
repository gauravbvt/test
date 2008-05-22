package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 15, 2008
 * Time: 3:18:16 PM
 */
class Association extends InformationAct {   // Creation of a relationship

    String relationshipName = ''
    Ref toAgent

    // Queries

    boolean createsMatchingRelationship(Relationship relationship) {
        if (relationship.name != relationshipName) return false
        if (relationship.agent && !this.playbook.agentImplied(relationship.agent, toAgent, this)) return false
        return true
    }

    // End queries

}