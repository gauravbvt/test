package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.InformationNeed
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.model.EventType
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
    Ref taskType
    List<InformationNeed> informationNeeds = []

    String toString() {
        return "Task of type $taskType"
    }

    // Return implied event type
    static Ref impliedEventType() {
        return ComputedRef.from(Task.class, 'makeImpliedEventType')
    }

    static EventType makeImpliedEventType() {
        EventType eventType =  new EventType(name:'task',              // note: model is null
                                             description:'A task',
                                             topics: impliedEventTypeTopics())
        use(NoSessionCategory) {eventType.narrow(Event.class.impliedEventType())}; // setting state of a computed ref
        return eventType
    }

    static List<String> impliedEventTypeTopics() {
        return InformationAct.class.impliedEventTypeTopics() + ['duration', 'task type', 'information need']
    }


    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'duration': return [duration.about()]
            case 'task type': return [taskType.about()]
            case 'information need': return informationNeeds.collect {need -> need.about()}
            default: return super.contentsAboutTopic(topic)
        }
    }
}