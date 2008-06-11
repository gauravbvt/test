package com.mindalliance.channels.playbook.ifm.info


import com.mindalliance.channels.playbook.ifm.spec.EventSpec
import com.mindalliance.channels.playbook.ifm.info.AbstractInformation

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 9:18:11 AM
*/
/* abstract*/ class InformationTemplate extends AbstractInformation {

    EventSpec eventSpec = new EventSpec() // about what kind of event

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['name']
    }


    String getName() {
        return toString()
    }

    String toString() {
        return "N2K about ${eventTypesString()}"
    }

    String makeLabel(int maxWidth) {
        String eventTypes = eventTypesString()
        String label = "Need info about\n ${eventTypes[0..Math.min(eventTypes.size()-1, maxWidth-1)]}"
        eventDetails.each {eoi ->
            label += "|${eoi.topic[0..Math.min(eoi.topic.size()-1, maxWidth-1)]}"
        }
        return label
    }

    private String eventTypesString() {
        String s = ""
        if (eventSpec) {
            eventSpec.eventTypes.each {et ->
                s += "${et.name},"
            }
        }
        if (s.endsWith(',')) {
            return s[0..s.size()-1]
        }
        else {
            return s
        }
    }
}