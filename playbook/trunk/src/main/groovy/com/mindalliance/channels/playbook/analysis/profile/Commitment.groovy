package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.sharing.SharingProtocol
import com.mindalliance.channels.playbook.ifm.sharing.SharingConstraints
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ifm.project.environment.SharingAgreement
import com.mindalliance.channels.playbook.ifm.Timing

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:11:06 PM
 */
class Commitment extends ProfileElement {

    SharingProtocol protocol = new SharingProtocol()
    SharingConstraints constraints = new SharingConstraints()
    Ref towardAgent
    Timing maxDelay = new Timing(amount:0)

    Commitment(InformationAct act, Ref agent, Ref towardAgent, SharingProtocol protocol, SharingConstraints constraints) {
        super(act, agent)
        this.protocol = protocol
        this.towardAgent = towardAgent
        this.constraints = constraints
    }

    Commitment(Playbook playbook, SharingAgreement sharingAgreement) {
        super(playbook, sharingAgreement, sharingAgreement.source)
        this.towardAgent = sharingAgreement.recipient
        this.protocol = sharingAgreement.protocol
        this.constraints = sharingAgreement.constraints
        this.maxDelay = sharingAgreement.maxDelay
    }

    Commitment(Playbook playbook, Ref agent, Ref towardAgent, SharingAgreement sharingAgreement) {
        super(playbook, sharingAgreement, agent)
        this.towardAgent = towardAgent
        this.protocol = sharingAgreement.protocol
        this.constraints = sharingAgreement.constraints
        this.maxDelay = sharingAgreement.maxDelay
    }

}