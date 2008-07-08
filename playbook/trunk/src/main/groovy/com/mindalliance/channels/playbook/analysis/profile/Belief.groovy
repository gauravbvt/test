package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ifm.Agent
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.playbook.Playbook

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:13:10 PM
 */
class Belief extends ProfileElement {

    Information information

    Belief(Playbook playbook, Agent agent, Duration start, Duration end, Information information) {
        super(playbook, agent, start, end)
        this.information = information
    }
}