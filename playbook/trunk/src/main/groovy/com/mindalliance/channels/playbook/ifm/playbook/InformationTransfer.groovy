package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.model.EventType
import com.mindalliance.channels.playbook.mem.NoSessionCategory

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 1:39:07 PM
 */
class InformationTransfer extends SharingAct {

    Ref mediumType

    String toString() {
        return "Transfer of $information"
    }

    // Return implied event type
    static Ref impliedEventType() {
        return ComputedRef.from(InformationTransfer.class, 'makeImpliedEventType')
    }

    static EventType makeImpliedEventType() {
        EventType eventType =  new EventType(name:'information transfer',              // note: model is null
                                             description:'An information transfer',
                                             topics: ['medium'])
        use(NoSessionCategory) {eventType.narrow(SharingAct.impliedEventType())}; // setting state of a computed ref
        return eventType
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'medium': return [mediumType.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }

}