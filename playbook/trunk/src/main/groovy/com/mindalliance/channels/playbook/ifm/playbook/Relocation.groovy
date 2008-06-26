package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.model.EventType
import com.mindalliance.channels.playbook.mem.NoSessionCategory

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 18, 2008
 * Time: 12:47:03 PM
 */
class Relocation extends InformationAct {     // a change of location

    Location location

    // Return implied event type
    static Ref impliedEventType() {
        return ComputedRef.from(Relocation.class, 'makeImpliedEventType')
    }

    static EventType makeImpliedEventType() {
        EventType eventType = new EventType(name: 'relocation',              // note: model is null
                description: 'A change of location',
                topics: impliedEventTypeTopics())
        use(NoSessionCategory) {eventType.narrow(Event.class.impliedEventType())}; // setting state of a computed ref
        return eventType
    }

    static List<String> impliedEventTypeTopics() {
        return InformationAct.class.impliedEventTypeTopics() + ['location']
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'location': return [location.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }



}