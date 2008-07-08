package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ifm.Agent
import org.joda.time.Duration

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:35:52 PM
 */
class Insight extends ProfileElement {   // an agent knows a profile element of another agent

    ProfileElement profileElement

    Insight(Playbook playbook, Agent agent, Duration start, Duration end, ProfileElement profileElement) {
        super(playbook, agent, start, end)
        this.profileElement = profileElement
    }
}