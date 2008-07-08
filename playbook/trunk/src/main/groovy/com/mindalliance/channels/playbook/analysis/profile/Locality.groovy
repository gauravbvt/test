package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.Agent
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.playbook.Playbook

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:04:57 PM
 */
class Locality extends ProfileElement {

    Location location

    Locality(Playbook playbook, Agent agent, Duration start, Duration end, Location location) {
        super(playbook, agent, start, end)
        this.location = location
    }

}