package com.mindalliance.channels.playbook.analysis.problem

import com.mindalliance.channels.playbook.ref.Referenceable

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 9, 2008
 * Time: 7:36:21 AM
 */
class Invalid extends Problem {

    private static final Map INVALID_TAGS = [
            actorMissing: "The actor is missing.",
            targetMissing: "The target agent is missing.",
            noSharedInfo: "The shared information is not defined.",
            senderIsReceiver: "The sender can not also be the receiver.",
            assigneeMissing: "No agent is assigned a responsibility.",
            noResponsibility: "The responsibility is not defined.",
            associateMissing: "The associate is missing.",
            noRelationshipName: "The relationship in unnamed.",
            noInformationNeed: "The information need is not defined.",
            noLocation: "The location is not defined.",
            noProtocol: "The sharing protocol is not defined.",
            protocolInfoSpec: "The sharing protocol does not define what information it is about.",
            agreementIncomplete: "The sharing agreement is not sufficienlty defined; it is missing a source, recipient and/or a sharing protocol.",
            policyIncomplete: "The policy is not sufficiently defined; it is missing a source, the recipient is not defined and/or the information the policy is about is not defined.",
            relationshipIncomplete: "The relationship is not sufficiently defined; it is missing the 'from', the 'to' and/or a name."
    ]

    Invalid(Referenceable referenceable, String tag) {
        super(referenceable, tag)
    }

    public String textForTag(String tag) {
        return (String)INVALID_TAGS[tag]
    }
}