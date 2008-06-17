package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.model.EventType
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
                                             description:'An association',
                                             topics: impliedEventTypeTopics())
        use(NoSessionCategory) {eventType.narrow(Event.class.impliedEventType())}; // setting state of a computed ref
        return eventType
    }

    static List<String> impliedEventTypeTopics() {
        return InformationAct.class.impliedEventTypeTopics() + ['relationship', 'to', 'reverse' ]
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