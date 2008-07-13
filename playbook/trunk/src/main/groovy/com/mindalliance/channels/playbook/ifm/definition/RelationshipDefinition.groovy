package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.matching.SemanticMatcher
import com.mindalliance.channels.playbook.support.Level

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 8:45:07 PM
 */
class RelationshipDefinition extends Definition {

    String relationshipName = '' // if empty, means any kind of relationship
    AgentSpecification withAgentSpecification = new AgentSpecification()    

    Class<? extends Bean> getMatchingDomainClass() {
        return Relationship.class
    }

    boolean matchesAll() {
        return !relationshipName && withAgentSpecification.matchesAll()
    }

    MatchResult match(Bean bean, InformationAct informationAct) {
        Relationship relationship = (Relationship)bean
        if (relationshipName) {
            SemanticMatcher semanticMatcher = SemanticMatcher.getInstance()
            Level level = semanticMatcher.semanticProximity(relationshipName, relationship.name)
            if (level < Level.HIGH) {
                return new MatchResult(matched:false, failures:["$relationship does not match specified kind $relationshipName ($level)"])
            }
        }
        if (relationship.toAgent as boolean && !withAgentSpecification.matches(relationship.toAgent.deref(), informationAct)) {
            return new MatchResult(matched:false, failures:["$relationship does not match specified target agent"])
        }
        return new MatchResult(matched:true)
    }

    MatchResult fullMatch(Bean bean, InformationAct informationAct) {
        return null;  // TODO
    }

    boolean implies(MatchingDomain matchingDomain) {
        RelationshipDefinition other = (RelationshipDefinition)matchingDomain
        if (other.matchesAll()) return true
        if (other.relationshipName && !SemanticMatcher.matches(relationshipName, other.relationshipName, Level.HIGH)) return false
        if (!withAgentSpecification.implies(other.withAgentSpecification)) return false
        return true;
    }

}