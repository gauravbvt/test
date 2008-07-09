package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.sharing.SharingProtocol
import com.mindalliance.channels.playbook.ifm.sharing.SharingConstraints

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
    Agent towardAgent

    Commitment(InformationAct act, Ref agent, Ref towardAgent, SharingProtocol protocol, SharingConstraints constraints) {
        super(act, agent)
        this.protocol = protocol
        this.towardAgent = (Agent)towardAgent.deref()
        this.constraints = constraints
    }

}