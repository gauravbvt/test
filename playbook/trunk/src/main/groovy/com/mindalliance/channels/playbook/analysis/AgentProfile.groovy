package com.mindalliance.channels.playbook.analysis

import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 20, 2008
 * Time: 10:11:12 AM
 */
class AgentProfile {

    Agent agent
    InformationAct informationAct // if informationAct == null => time +0

    static AgentProfile forAgentAt(Agent agent, InformationAct informationAct) {
        return new AgentProfile(agent:agent, informationAct:informationAct)
    }

    Location getLocation() {
        return new Location() // TODO
    }

    List<Relationship> getRelationships() {
        return [] // TODO
    }

}