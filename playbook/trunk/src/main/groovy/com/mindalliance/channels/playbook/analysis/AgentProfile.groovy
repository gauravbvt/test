package com.mindalliance.channels.playbook.analysis

import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.support.drools.RuleBaseSession
import com.mindalliance.channels.playbook.analysis.profile.Link
import com.mindalliance.channels.playbook.query.Query

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
        List results = RuleBaseSession.query("agentLocation", [agent.id, informationAct.id], "_location")
        if (results) {
            assert results.size() == 1
            return (Location)results[0]
        }
        else {
            return agent.location
        }
    }

    List<Relationship> getRelationships() {
        List<Relationship> relationships = (List<Relationship>)Query.execute(informationAct.getProject(), "findAllRelationshipsOf", agent.reference)
        List<Link> results = (List<Link>)RuleBaseSession.query("agentLinks", [agent.id, informationAct.id], "_link")
        for (Link link : results) {
            Relationship rel = new Relationship(fromResource: agent.reference, toResource:link.toAgent.reference, name:link.relationshipName)
            relationships.add(rel)
        }
        return relationships
        }

}