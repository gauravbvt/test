package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.info.SharingProtocol
import com.mindalliance.channels.playbook.ifm.info.SharingConstraints
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2008
 * Time: 9:27:02 AM
 */
class SharingCommitment extends FlowAct {

    SharingProtocol protocol = new SharingProtocol()
    SharingConstraints constraints = new SharingConstraints()
    Ref approvedBy // a position, if any

    // Queries

    List<String> findAllTopics() {
        List<ElementOfInformation> eois = protocol.informationTemplate.eventDetails
        return eois.collect {eoi -> eoi.topic}
    }
    
    // end queries
}