package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.info.InformationNeed
import com.mindalliance.channels.playbook.ifm.Agent
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.playbook.Playbook

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:02:55 PM
 */
class NeedToKnow extends ProfileElement {

    InformationNeed informationNeed

    NeedToKnow(Playbook playbook, Agent agent, Duration start, Duration end, InformationNeed informationNeed) {
        super(playbook, agent, start, end)
        this.informationNeed = informationNeed
    }

}