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

    List<Ref> actors = []  // one agent or a team of agents

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['duration', 'flowAct', 'sharingAct', 'actingAgents'])
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

    String getActingAgents() {
        String names = ''
        actors.each {agent -> names += "${agent.name},"}
        return (names.size() > 1 && names.endsWith(',')) ? names[0..names.size()-2] : names
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
            case 'actor': return actors.collect{it.about()}
            default: return super.contentsAboutTopic(topic)
        }
    }

    String actorNames() {
        String names = ''
        actors.each {agent -> names += (agent.toString() + ",")}
        if (names.size() > 1 && names.endsWith(',')) return names[0..names.size()-2]
        if (!names) return "NO ONE"
        return names
    }


}