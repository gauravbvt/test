package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification
import com.mindalliance.channels.playbook.ifm.definition.EventSpecification
import com.mindalliance.channels.playbook.ifm.Defineable
import com.mindalliance.channels.playbook.ifm.sharing.SharingProtocol
import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ifm.definition.InformationDefinition
import com.mindalliance.channels.playbook.support.RefUtils

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
        return "${critical ? ' (critical)' : ''} info need about ${informationSpec.about()}"
    }

    boolean isDefined() {
        return !informationSpec.matchesAll()
    }

    boolean isAboutSpecificEvents() {
        return !informationSpec.eventSpec.enumeration.isEmpty()
    }

    String makeLabel(int maxWidth) {
        String eventSpecDescription = informationSpec.eventSpec.description
        String label = "Need info\n"
        if (eventSpecDescription) label += "${RefUtils.summarize(eventSpecDescription, maxWidth)}"
        informationSpec.elementsOfInformation.each {eoi ->
            label += "|${RefUtils.summarize(eoi.topic, maxWidth)}"
        }
        return label
    }

}