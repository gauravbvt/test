package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 18, 2008
 * Time: 4:12:58 PM
 */
class AgentSpecification extends Specification {

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['roles'])
    }

    Class<? extends Bean> getMatchingDomainClass() {
        return Agent.class;
    }

    List<Ref> getRoles() {
        List<Ref> roles = []
        definitions.each {definition ->
            roles.addAll(((AgentDefinition)definition).roles)
        }
        return roles
    }

}