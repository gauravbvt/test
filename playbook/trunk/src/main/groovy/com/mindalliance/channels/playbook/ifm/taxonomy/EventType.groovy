package com.mindalliance.channels.playbook.ifm.taxonomy

import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 12:53:22 PM
*/
class EventType extends Category {

    List<String> topics = [] // what can usually be known about elements of this type

    List<String> allTopics() {
        List<String> all = []
        all.addAll(topics)
        narrowedTypes.each {nt ->
            all.addAll(nt.allTopics())
        }
        return all
    }

    // Queries

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

    Ref findNarrowedEventTypeWithTopic(String topic) {
        Ref narrowedEventType = null
        narrowedTypes.any {nt ->
            if (nt.topics.contains(topic)) {
                narrowedEventType = nt
            }
            else {
                narrowedEventType = nt.findNarrowedEventTypeWithTopic(topic)
            }
        }
        return narrowedEventType
    }
}