package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.profile.AgentProfile
import com.mindalliance.channels.playbook.ifm.Jurisdictionable

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 8:29:45 PM
 */
class AgentDefinition extends Definition { // Definition of a kind of agent

    List<Ref> roles = [] // ANDed  -- classification by roles
    OrganizationSpecification organizationSpecification = new OrganizationSpecification()
    LocationDefinition locationDefinition = new LocationDefinition()
    LocationDefinition jurisdictionDefinition = new LocationDefinition() // applies only if agent has a jurisdiction
    List<RelationshipDefinition> relationshipDefinitions = []  // ORed

    public Class<? extends Bean> getMatchingDomainClass() {
        return Agent.class; 
    }

    public boolean matchesAll() {
        return roles.isEmpty() && organizationSpecification.matchesAll() && locationDefinition.matchesAll() && jurisdictionSpecification.matchesAll() && relationshipDefinitions.isEmpty()
    }

    public MatchResult match(Bean bean, InformationAct informationAct) {
        Agent agent = (Agent)bean
        if (!roles.every {srole -> agent.roles.any {role -> role.implies(srole)}}) {  // if not all of the prescribed roles is matched at least one of the agent's roles, then fail
            return new MatchResult(matched:false, failures:["$agent does not match all specified roles"])
        }
        if (agent instanceof Jurisdictionable) {
            if (!jurisdictionDefinition.matches(((Jurisdictionable)agent).jurisdiction, informationAct)) {
                return new MatchResult(matched:false, failures:["Jurisdiction of $agent does not match"])
            }
        }
        if (!locationDefinition.matchesAll() || relationshipDefinitions)  {
            AgentProfile agentProfile = AgentProfile.forAgentAt(agent, informationAct)
            if (!locationDefinition.matches(agentProfile.location, informationAct)) {
                return new MatchResult(matched:false, failures:["Location of $agent does not match"])
            }
            if (!relationshipDefinitions.any {relDef -> agentProfile.relationships.any {aRel -> relDef.matches(aRel, informationAct)}}) { // if none of the prescribed relationships is matched, then fail
                return new MatchResult(matched:false, failures:["$agent matches none of the prescribed relationships"])
            }
        }
        return new MatchResult(matched:true)
    }

    public MatchResult fullMatch(Bean bean, InformationAct informationAct) {
        return null;  // TODO
    }

    public boolean narrows(MatchingDomain matchingDomain) {
        return false;  // TODO
    }

}