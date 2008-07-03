package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification
import com.mindalliance.channels.playbook.ifm.definition.EventSpecification

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 27, 2008
 * Time: 9:56:23 PM
 */
class InformationNeed extends AbstractInformation {

    EventSpecification eventSpec = new EventSpecification() // about what (kind of) events
    Ref agent // agent who needs the information
    AgentSpecification sourceSpec = new AgentSpecification() // specification fo acceptable source of needed information
    Ref event // XOR with InformationTemplate.eventSpec -- need is about a specific (vs specified) event
    Timing deadline = new Timing(amount: 0) // deadline of 0 means indefinite. Information need nullifed after deadline.

    String getName() {
        return toString()
    }
    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['name', 'aboutSpecificEvent'])
    }

    String toString() {
        return "N2K about: ${eventSpec.description.replaceAll('\\n', ' ')}"
    }

    String makeLabel(int maxWidth) {
        String eventSpecDescription = eventSpec.description
        String label = "Need info about\n"
        if (eventSpecDescription) label += "${eventSpecDescription[0..Math.min(eventSpecDescription.size()-1, maxWidth-1)]}"
        eventDetails.each {eoi ->
            label += "|${eoi.topic[0..Math.min(eoi.topic.size()-1, maxWidth-1)]}"
        }
        return label
    }

}