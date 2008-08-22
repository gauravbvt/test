package com.mindalliance.channels.playbook.ifm.project.environment

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.ifm.sharing.SharingProtocol
import com.mindalliance.channels.playbook.ifm.sharing.SharingConstraints
import com.mindalliance.channels.playbook.ifm.Described
import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ifm.Named

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 10:43:29 AM
*/
class SharingAgreement extends ProjectElement implements Named, Described {

    String name = ''
    String description = ''
    Ref source   // readOnly -- set on creation  -- organization
    Ref recipient     // readOnly -- set on creation   -- other organization
    SharingProtocol protocol = new SharingProtocol(incoming:false)
    SharingConstraints constraints = new SharingConstraints()
    Timing maxDelay = new Timing(amount:0)
    boolean formalized = false // else informal


    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['name'])
    }

    Set keyProperties() {
        return (super.keyProperties() + ['description']) as Set
    }

    String toString() {
        return (name) ? name : 'Unnamed'
    }

    String about() {
        String sourceName = (source as boolean) ? source.name : "Undefined source"
        String recipientName = (recipient as boolean) ? recipient.name : "undefined recipient"
        return "${sourceName} shall ${protocol.delivery} ${recipientName}"
    }

    // queries

    List<String> findAllTopics() {
        List<ElementOfInformation> eois = protocol.informationSpec.elementsOfInformation
        return eois.collect {eoi -> eoi.topic}
    }
    // end queries

}