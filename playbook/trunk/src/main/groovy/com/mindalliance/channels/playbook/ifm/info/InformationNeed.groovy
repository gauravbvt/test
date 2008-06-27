package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ifm.info.InformationTemplate
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.spec.AgentSpec
import com.mindalliance.channels.playbook.ifm.spec.EventSpec
import com.mindalliance.channels.playbook.ifm.Timing

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 27, 2008
 * Time: 9:56:23 PM
 */
class InformationNeed extends InformationTemplate {

    Ref agent // agent who needs the information
    AgentSpec sourceSpec = new AgentSpec() // specification fo acceptable source of needed information
    Ref event // XOR with InformationTemplate.eventSpec -- need is about a specific (vs specified) event
    Timing deadline = new Timing(amount: 0) // deadline of 0 means indefinite. Information need nullifed after deadline.
    // eventSpec.locationSpec =:= <profile>.location    // context is the profile with element holding the information being matched

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['aboutSpecificEvent']
    }

    boolean isAboutSpecificEvent() {
        return event != null
    }

    void setEvent(Ref ref) {
        event = ref
        if (ref) {
            eventSpec = new EventSpec()
        }
    }

    void setEventSpec(EventSpec eventSpec) {
        super.setEventSpec(eventSpec)
        if (eventSpec.isDefined()) {
            event = null
        }
    }

    String makeLabel(int maxWidth) {
        String label = "Need info about\n"
        if (event as boolean) {
            String eventName = event.name
            label += "${eventName[0..Math.min(eventName.size()-1, maxWidth-1)]}"
        }
        else {
            String eventTypesString = eventTypesString()
            if (eventTypesString) {
                label += "${eventTypesString[0..Math.min(eventTypesString.size()-1, maxWidth-1)]}"
            }
            else {
                label += "any kind of event"
            }
        }
        eventDetails.each {eoi ->
            label += "|${eoi.topic[0..Math.min(eoi.topic.size()-1, maxWidth-1)]}"
        }
        return label
    }


}