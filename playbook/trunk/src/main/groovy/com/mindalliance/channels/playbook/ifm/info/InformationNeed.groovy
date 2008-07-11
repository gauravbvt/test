package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification
import com.mindalliance.channels.playbook.ifm.definition.EventSpecification
import com.mindalliance.channels.playbook.ifm.Defineable
import com.mindalliance.channels.playbook.ifm.sharing.SharingProtocol
import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ifm.definition.InformationDefinition

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 27, 2008
 * Time: 9:56:23 PM
 */
class InformationNeed extends BeanImpl implements Defineable {

    InformationDefinition informationSpec = new InformationDefinition()
    boolean critical = false
    Timing deadline = new Timing(amount: 0) // relative deadline of 0 means indefinite. Information need nullifed after deadline.

    static InformationNeed fromSharingProtocol(SharingProtocol protocol) {
        InformationNeed need = new InformationNeed(informationSpec: (InformationDefinition)protocol.informationSpec.copy())
        return need
    }

    String getName() {
        return toString()
    }
    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['name', 'aboutSpecificEvents', 'defined'])
    }

    String toString() {
        return "$informationSpec" + (critical ? ' (critical)' : '')
    }

    boolean isDefined() {
        return !informationSpec.matchesAll()
    }

    boolean isAboutSpecificEvents() {
        return !informationSpec.eventSpec.enumeration.isEmpty()
    }

    String makeLabel(int maxWidth) {
        String eventSpecDescription = informationSpec.eventSpec.description
        String label = "Need info about\n"
        if (eventSpecDescription) label += "${eventSpecDescription[0..Math.min(eventSpecDescription.size()-1, maxWidth-1)]}"
        informationSpec.elementsOfInformation.each {eoi ->
            label += "|${eoi.topic[0..Math.min(eoi.topic.size()-1, maxWidth-1)]}"
        }
        return label
    }

}