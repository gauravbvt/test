package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.Responsibility
import com.mindalliance.channels.playbook.ifm.Agent
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.playbook.Playbook

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:07:26 PM
 */
class Assignment extends ProfileElement {

    Responsibility responsibility

    Assignment(Playbook playbook, Agent agent, Duration start, Duration end, Responsibility responsibility) {
        super(playbook, agent, start, end)
        this.responsibility = responsibility
    }

}