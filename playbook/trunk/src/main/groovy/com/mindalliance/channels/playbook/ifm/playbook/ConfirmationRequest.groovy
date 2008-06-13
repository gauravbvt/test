package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.spec.AgentSpec
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 1:39:42 PM
 */
class ConfirmationRequest extends SharingAct {

    AgentSpec sourceSpec = new AgentSpec() // source is specified...
    Ref sourceAgent // xor is identified

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['sourceSpecified'])
    }

    void setSourceSpec(AgentSpec agentSpec) {
        AgentSpec old = sourceSpec
        if (agentSpec.isDefined()) {
            sourceAgent = null
        }
        propertyChanged("sourceSpec", old, agentSpec)
    }

    void setSourceAgent(Ref agent) {
        Ref old = sourceAgent
        if(agent != null) {
            sourceSpec = new AgentSpec()
        }
        propertyChanged("sourceAgent", old, agent)
    }

    boolean isSourceSpecified() {
        return sourceAgent == null
    }
}