package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.taxonomy.EventType
import com.mindalliance.channels.playbook.mem.NoSessionCategory

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:37:38 PM
*/
abstract class SharingAct extends FlowAct {

    Information information = new Information()

    String toString() {
        return "${this.type} of $information"
    }

    @Override
    boolean isSharingAct() {
        return true
    }

    boolean hasInformation() {
        return true
    }

    // Return implied event type
    static Ref impliedEventType() {
        return ComputedRef.from(SharingAct.class, 'makeImpliedEventType')
    }

    static EventType makeImpliedEventType() {
        EventType eventType =  new EventType(name:'information sharing act',              // note: model is null
                                             description:'An information sharing act',
                                             topics: ['information'])
        use(NoSessionCategory) {eventType.narrow(FlowAct.impliedEventType())}; // setting state of a computed ref
        return eventType
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'information': return [information.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }

}