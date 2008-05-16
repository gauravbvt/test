package com.mindalliance.channels.playbook.ifm.project.environment

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.ifm.info.SharingProtocol
import com.mindalliance.channels.playbook.ifm.info.SharingConstraints
import com.mindalliance.channels.playbook.ifm.Describable
import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 10:43:29 AM
*/
class SharingAgreement extends ProjectElement implements Describable {

    static final List<String> deliveries = ['notify', 'answer']

    String description = ''
    Ref source   // readOnly -- set on creation
    Ref recipient     // readOnly -- set on creation
    SharingProtocol protocol = new SharingProtocol()
    SharingConstraints constraints = new SharingConstraints()
    boolean formalized = false // else informal


    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['deliveries', 'name'])
    }

    String toString() {
        return "${source.name} shall ${protocol.delivery} ${recipient.name}"
    }

    String getName() {
        return toString()
    }

    // queries

    List<String> findAllTopics() {
        List<ElementOfInformation> eois = protocol.informationTemplate.eventDetails
        return eois.collect {eoi -> eoi.topic}
    }
    // end queries

}