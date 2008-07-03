package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.model.EventType
import com.mindalliance.channels.playbook.mem.NoSessionCategory

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 1:27:48 PM
 */
abstract class InformationAct extends Event {

    Ref actorAgent // an agent, i.e. group or team within the scope of the Playbook, or a resource in the scope of the Playbook's Project

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['duration', 'flowAct', 'sharingAct']
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

    String toString() {
        return "${this.type}"    // TODO - do better than this
    }

    boolean hasInformation() {
        return false
    }

    // Return implied event type
    static Ref impliedEventType() {
        return ComputedRef.from(InformationAct.class, 'makeImpliedEventType')
    }

    static EventType makeImpliedEventType() {
        EventType eventType =  new EventType(name:'information act',              // note: model is null
                                             description:'An information act',
                                             topics: ['actor'])
        use(NoSessionCategory) {eventType.narrow(Event.impliedEventType())}; // setting state of a computed ref
        return eventType
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'actor': return [actorAgent.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }


}