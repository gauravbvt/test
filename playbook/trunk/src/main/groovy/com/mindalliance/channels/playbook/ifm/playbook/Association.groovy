package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.taxonomy.EventType
import com.mindalliance.channels.playbook.mem.NoSessionCategory

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

    // Return implied event type
    static Ref impliedEventType() {
        return ComputedRef.from(Association.class, 'makeImpliedEventType')
    }

    static EventType makeImpliedEventType() {
        EventType eventType =  new EventType(name:'association',              // note: model is null
                                             description:'A new relationship',
                                             topics: ['relationship', 'to', 'reverse' ])
        use(NoSessionCategory) {eventType.narrow(InformationAct.impliedEventType())}; // setting state of a computed ref
        return eventType
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'relationship': return [relationshipName]
            case 'to': return [toAgent.about()]
            case 'reverse': return [reverseRelationshipName]
            default: return super.contentsAboutTopic(topic)
        }
    }

    String toString() {
        return "Association: ${actorAgent} $relationshipName ${toAgent} "
    }

    // Queries

    boolean createsMatchingRelationship(Relationship relationship) {
        if (relationship.name == relationshipName &&  // TODO -- apply semantic matching
                this.playbook.agentImplied(relationship.fromAgent, actorAgent, this.reference)
                        && this.playbook.agentImplied(relationship.toAgent, toAgent, this.reference)) return true
        if (relationship.name == reverseRelationshipName &&
                this.playbook.agentImplied(relationship.fromAgent, toAgent, this.reference)
                        && this.playbook.agentImplied(relationship.toAgent, actorAgent, this.reference)) return true
        return false
    }

    // End queries

}