package com.mindalliance.channels.playbook.ifm.model

import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 12:53:22 PM
*/
class EventType extends ElementType {

    List<String> topics = [] // what can usually be known about elements of this type

    static List<String> findAllTopicsIn(List<Ref> eventTypes) {
        Set<String> topics = new HashSet<String>()
        Set<Ref> impliedEventTypes = new HashSet<Ref>()
        eventTypes.each {eventType ->
            impliedEventTypes.add(eventType)
            impliedEventTypes.addAll(eventType.ancestors())
        }
        impliedEventTypes.each {eventType ->
            topics.addAll(eventType.topics)
        }
        List<String> sortedTopics = topics as List
        return sortedTopics.sort()
    }
}