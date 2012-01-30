package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.taxonomy.EventType
import com.mindalliance.channels.playbook.mem.NoSessionCategory
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 1:39:42 PM
 */
class ConfirmationRequest extends SharingAct {

    AgentSpecification sourceSpec = new AgentSpecification() // source is specified...

    @Override
    List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['sourceSpecified'])
    }

    // Return implied event type
    static Ref implicitEventType() {
        return ComputedRef.from(ConfirmationRequest.class, 'makeImplicitEventType')
    }

    static EventType makeImplicitEventType() {
        EventType eventType = new EventType(name: 'confirmation request',              // note: model is null
                description: 'A confirmation request',
                topics: ['source'])
        use(NoSessionCategory) {eventType.narrow(SharingAct.implicitEventType())}; // setting state of a computed ref
        return eventType
    }


   List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'source': return [sourceSpec.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }


}