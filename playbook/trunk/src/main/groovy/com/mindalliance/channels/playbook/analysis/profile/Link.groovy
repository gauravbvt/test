package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.ifm.Agent
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.playbook.Playbook

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:09:34 PM
 */
class Link extends ProfileElement {

    Relationship relationship

    Link(Playbook playbook, Agent agent, Duration start, Duration end, Relationship relationship) {
        super(playbook, agent, start, end)
        this. relationship = relationship
    }

}