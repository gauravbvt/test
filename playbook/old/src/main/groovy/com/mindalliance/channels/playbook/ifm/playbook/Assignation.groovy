package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.Responsibility
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.mem.NoSessionCategory
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.taxonomy.EventType

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:44:12 PM
*/
class Assignation extends FlowAct {  // communication of a responsibility (the target may not be the assignee)

    Responsibility responsibility = new Responsibility()
    Ref assigneeAgent

    String about() {
        return "Assignation of ${responsibility.toString()}"
    }

    // Return implied event type
    static Ref implicitEventType() {
        return ComputedRef.from(Assignation.class, 'makeImplicitEventType')
    }

    static EventType makeImplicitEventType() {
        EventType eventType =  new EventType(name:'assignation',              // note: model is null
                                             description:'An assignation of responsibility',
                                             topics: ['responsibility', 'assignee'])
        use(NoSessionCategory) {eventType.narrow(FlowAct.implicitEventType())}; // setting state of a computed ref
        return eventType
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'responsibility': return [responsibility.about()]
            case 'assignee': return [assigneeAgent.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }



}