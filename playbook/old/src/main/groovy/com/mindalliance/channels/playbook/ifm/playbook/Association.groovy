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

    // Return implied event type
    static Ref implicitEventType() {
        return ComputedRef.from(Association.class, 'makeImplicitEventType')
    }

    static EventType makeImplicitEventType() {
        EventType eventType =  new EventType(name:'association',              // note: model is null
                                             description:'A new relationship',
                                             topics: ['relationship', 'to' ])
        use(NoSessionCategory) {eventType.narrow(InformationAct.implicitEventType())}; // setting state of a computed ref
        return eventType
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'relationship': return [relationshipName]
            case 'to': return [toAgent.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }

    String about() {
        return "Association: ${this.actorNames()} $relationshipName ${toAgent} "
    }

    // Queries


    // End queries

}