package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ifm.model.EventType
import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 1:27:48 PM
 */
/* abstract*/ class InformationAct extends Event {

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

    static List<String> impliedEventTypeTopics() {
        return ['actor']
    }

    // Create information about this information act
    Information makeInformation() {
        Information info = new Information(event: this.reference)
        Ref eventType = this.class.impliedEventType()
        info.eventTypes.add(eventType)
        eventType.allTopics().each {topic ->
            List<String> contents = contentsAboutTopic(topic)
            contents.each {content ->
                ElementOfInformation eoi = new ElementOfInformation(topic:topic, content: content)
                info.eventDetails.add(eoi)
            }
        }
        return info
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'actor': return [actorAgent.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }


}