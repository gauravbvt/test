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
* Time: 1:41:27 PM
*/
abstract class FlowAct extends InformationAct {

    Ref targetAgent

    @Override
    boolean isFlowAct() {
        return true
    }

    // Return implied event type
    static Ref impliedEventType() {
        return ComputedRef.from(InformationAct.class, 'makeImpliedEventType')
    }

    static EventType makeImpliedEventType() {
        EventType eventType =  new EventType(name:'flow information act',              // note: model is null
                                             description:'A flow information act',
                                             topics: ['target'])
        use(NoSessionCategory) {eventType.narrow(InformationAct.impliedEventType())}; // setting state of a computed ref
        return eventType
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'target': return [targetAgent.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }



}