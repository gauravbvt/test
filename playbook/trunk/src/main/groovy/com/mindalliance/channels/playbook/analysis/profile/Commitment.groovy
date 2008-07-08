package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.project.environment.SharingAgreement
import com.mindalliance.channels.playbook.ifm.Agent
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.playbook.Playbook

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:11:06 PM
 */
class Commitment extends ProfileElement {

    SharingAgreement sharingAgreement

    Commitment(Playbook playbook, Agent agent, Duration start, Duration end, SharingAgreement sharingAgreement) {
        super(playbook, agent, start, end)
        this.sharingAgreement = sharingAgreement
    }

}