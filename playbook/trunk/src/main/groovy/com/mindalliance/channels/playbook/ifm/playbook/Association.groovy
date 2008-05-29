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
    String reverseRelationshipName = ''

    // Queries

    boolean createsMatchingRelationship(Relationship relationship) {
        if (relationship.name == relationshipName &&
                this.playbook.agentImplied(relationship.fromAgent, actorAgent, this
                        && this.playbook.agentImplied(relationship.toAgent, toAgent))) return true
        if (relationship.name == reverseRelationshipName &&
                this.playbook.agentImplied(relationship.fromAgent, toAgent, this
                        && this.playbook.agentImplied(relationship.toAgent, actorAgent))) return true
        return false
    }

    // End queries

}