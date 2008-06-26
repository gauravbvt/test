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
class InformationTemplate extends AbstractInformation {

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
        String eventTypesString = eventTypesString()
        String label = "Need info about\n"
        if (eventTypesString) label += "${eventTypesString[0..Math.min(eventTypesString.size()-1, maxWidth-1)]}"
        eventDetails.each {eoi ->
            label += "|${eoi.topic[0..Math.min(eoi.topic.size()-1, maxWidth-1)]}"
        }
        return label
    }

    String eventTypesString() {
        String s = ""
        if (eventSpec) {
            eventSpec.eventTypes.each {et ->
                s += "${et.name},"
            }
        }
        if (s.endsWith(',')) {
            return s[0..s.size()-2]
        }
        else {
            return s
        }
    }
}