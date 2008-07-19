package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.InformationNeed
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.taxonomy.EventType
import com.mindalliance.channels.playbook.mem.NoSessionCategory

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:44:43 PM
*/
class Task extends InformationAct {

    Timing duration = new Timing(amount:0)
    List<Ref> taskTypes = []
    List<InformationNeed> informationNeeds = []
    List<String> specificPurposes = [] // specific purposes (vs thoses stated in TaskTypes)

    String toString() {
        return "a task"
    }

    // Return implied event type
    static Ref implicitEventType() {
        return ComputedRef.from(Task.class, 'makeImplicitEventType')
    }

    static EventType makeImplicitEventType() {
        EventType eventType =  new EventType(name:'task',              // note: model is null
                                             description:'A task',
                                             topics: ['duration', 'task type', 'information need'])
        use(NoSessionCategory) {eventType.narrow(InformationAct.implicitEventType())}; // setting state of a computed ref
        return eventType
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'duration': return [duration.about()]
            case 'task type': return taskTypes.collect{it.about()}
            case 'information need': return informationNeeds.collect {it.about()}
            default: return super.contentsAboutTopic(topic)
        }
    }
}