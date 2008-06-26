package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.Agent

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 5:28:48 PM
 */
class AgentSpecification extends Specification {

    public Class<? extends Bean> getMatchingDomainClass() {
        return Agent.class
    }
}