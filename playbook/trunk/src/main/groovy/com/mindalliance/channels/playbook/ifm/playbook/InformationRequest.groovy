package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.info.InformationNeed
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.model.EventType
import com.mindalliance.channels.playbook.mem.NoSessionCategory

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:43:24 PM
*/
class InformationRequest extends FlowAct  {

    InformationNeed informationNeed = new InformationNeed()

    String toString() {
        return "Request for $informationNeed"
    }

    // Return implied event type
    static Ref impliedEventType() {
        return ComputedRef.from(InformationRequest.class, 'makeImpliedEventType')
    }

    static EventType makeImpliedEventType() {
        EventType eventType =  new EventType(name:'information request',              // note: model is null
                                             description:'An information request',
                                             topics: ['information need'])
        use(NoSessionCategory) {eventType.narrow(FlowAct.impliedEventType())}; // setting state of a computed ref
        return eventType
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'information need': return [informationNeed.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }

}