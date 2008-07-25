package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.sharing.SharingProtocol
import com.mindalliance.channels.playbook.mem.NoSessionCategory
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.taxonomy.EventType

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2008
 * Time: 9:26:19 AM
 */
class SharingRequest extends FlowAct {

    SharingProtocol protocol = new SharingProtocol()

    String about() {
        return "Request to commit to sharing ${protocol.informationSpec}"
    }

    // Return implied event type
    static Ref implicitEventType() {
        ComputedRef a
        return ComputedRef.from(SharingRequest.class, 'makeImplicitEventType')
    }

    static EventType makeImplicitEventType() {
        EventType eventType =  new EventType(name:'sharing request',              // note: model is null
                                             description:'Requesting a commitment to share information',
                                             topics: ['protocol'])
        use(NoSessionCategory) {eventType.narrow(FlowAct.implicitEventType())}; // setting state of a computed ref
        return eventType
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'protocol': return [protocol.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }
}