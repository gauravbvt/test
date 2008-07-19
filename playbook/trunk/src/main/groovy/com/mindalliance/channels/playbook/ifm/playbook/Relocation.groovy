package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.taxonomy.EventType
import com.mindalliance.channels.playbook.mem.NoSessionCategory

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 18, 2008
 * Time: 12:47:03 PM
 */
class Relocation extends InformationAct {     // a change of location

    Location location = new Location()

    // Return implied event type
    static Ref implicitEventType() {
        return ComputedRef.from(Relocation.class, 'makeImplicitEventType')
    }

    static EventType makeImplicitEventType() {
        EventType eventType = new EventType(name: 'relocation',              // note: model is null
                description: 'A change of location',
                topics: ['location'])
        use(NoSessionCategory) {eventType.narrow(InformationAct.implicitEventType())}; // setting state of a computed ref
        return eventType
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'location': return [location.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }



}