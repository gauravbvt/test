package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.taxonomy.EventType
import com.mindalliance.channels.playbook.mem.NoSessionCategory

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 1:27:48 PM
 */
abstract class InformationAct extends Event {

    Ref actorAgent // an agent, i.e. group within the scope of the Playbook, or a resource in the scope of the Playbook's Project

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['duration', 'flowAct', 'sharingAct'])
    }

    boolean isInformationAct() {
        return true
    }

    boolean isFlowAct() {
        return false
    }

    boolean isSharingAct() {
        return false
    }

    Timing getDuration() {
        return new Timing(amount: 0)
    }

    boolean hasInformation() {
        return false
    }

    // Return implied event type
    static Ref implicitEventType() {
        return ComputedRef.from(InformationAct.class, 'makeImplicitEventType')
    }

    static EventType makeImplicitEventType() {
        EventType eventType =  new EventType(name:'information act',              // note: model is null
                                             description:'An information act',
                                             topics: ['actor'])
        use(NoSessionCategory) {eventType.narrow(Event.implicitEventType())}; // setting state of a computed ref
        return eventType
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'actor': return [actorAgent.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }


}