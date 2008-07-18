package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.sharing.SharingProtocol
import com.mindalliance.channels.playbook.ifm.sharing.SharingConstraints
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ifm.taxonomy.EventType
import com.mindalliance.channels.playbook.mem.NoSessionCategory

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

    String toString() {
        return "Commitment to share ${protocol.informationSpec}"
    }

    // Return implied event type
    static Ref impliedEventType() {
        ComputedRef a
        return ComputedRef.from(SharingCommitment.class, 'makeImpliedEventType')
    }

    static EventType makeImpliedEventType() {
        EventType eventType =  new EventType(name:'sharing commitment',              // note: model is null
                                             description:'Committing to share',
                                             topics: ['protocol', 'constraints', 'approved by'])
        use(NoSessionCategory) {eventType.narrow(FlowAct.impliedEventType())}; // setting state of a computed ref
        return eventType
    }

    List<String> contentsAboutTopic(String topic) {
        switch (topic) {
            case 'protocol': return [protocol.about()]
            case 'constraints': return [constraints.about()]
            case 'approved by': return [approvedBy.about()]
            default: return super.contentsAboutTopic(topic)
        }
    }

    // Queries

    List<String> findAllTopics() {
        List<ElementOfInformation> eois = protocol.informationSpec.eventDetails
        return eois.collect {eoi -> eoi.topic}
    }
    
    // end queries
}