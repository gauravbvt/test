package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.project.resources.Resource
import com.mindalliance.channels.playbook.analysis.AgentProfile
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.project.resources.OrganizationResource
import com.mindalliance.channels.playbook.ifm.project.resources.Organization
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 8:29:45 PM
 */
class ResourceDefinition extends Definition { // Definition of a domain of Resources

    String type = '' // either Person, System, Job, Position, Organization, or any if empty
    LocationDefinition locationDefinition = new LocationDefinition() // resource is in a location matching this
    OrganizationSpecification organizationSpec = new OrganizationSpecification() // organization owning specified Resource
    List<RelationshipDefinition> relationshipDefinitions = []  // ORed -- and is in a relationship matching one of these

    static List<String> typeChoices() {
        return ["Person", "System", "Organization", "Position", "Job"]
    }

    Class<? extends Bean> getMatchingDomainClass() {
        return Resource.class;
    }

    boolean matchesAll() {
        return type.isEmpty() && locationDefinition.matchesAll() && organizationSpec.matchesAll() && relationshipDefinitions.isEmpty()
    }

    MatchResult match(Bean bean, InformationAct informationAct) {
        Resource resource = (Resource) bean
        if (typeMatches(resource)) {
            return new MatchResult(matched: false, failures: ["$resource type does not match"])
        }
        if (!organizationSpec.matchesAll()) {
            if (resource.isOrganizationResource()) { // System, Position or Job owned by matching Organization?
                Organization organization = (Organization) (((OrganizationResource) resource).getOrganization()).deref()
                if (!organizationSpec.matches(organization, informationAct)) {
                    return new MatchResult(matched: false, failures: ["The organization that owns $resource does not match organization specification"])
                }
            }
            if (resource.isAnOrganization()) { // Organization has matching ancestor?
                List<Ref> ancestors = ((Organization) resource).getAncestors()
                if (!ancestors.any {org -> org as boolean && organizationSpec.matches(org.deref(), informationAct)}) {
                    return new MatchResult(matched: false, failures: ["$resource does not have a matching parent organization (direct or indirect)"])
                }
            }
            if (resource.isAPerson()) {  //  Fail -- Person is not owned by an organization (in IFM at least...)
                return new MatchResult(matched: false, failures: ["$resource is a person and not owned by an organization"])
            }
        }
        if (!locationDefinition.matchesAll() || relationshipDefinitions) {
            AgentProfile agentProfile = AgentProfile.forAgentAt((Agent) resource, informationAct)
            if (!locationDefinition.matches(agentProfile.location, informationAct)) {
                return new MatchResult(matched: false, failures: ["Location of $resource does not match"])
            }
            if (!relationshipDefinitions.any {relDef -> agentProfile.relationships.any {aRel -> relDef.matches(aRel, informationAct)}}) { // if none of the prescribed relationships is matched, then fail
                return new MatchResult(matched: false, failures: ["$resource matches none of the prescribed relationships"])
            }
        }
        return new MatchResult(matched: true)
    }

    protected boolean typeMatches(Resource resource) {
        return !type.isEmpty() && resource.type != type
    }

    boolean implies(MatchingDomain matchingDomain) {
        ResourceDefinition other = (ResourceDefinition) matchingDomain
        if (other.matchesAll()) return true
        if (!other.type.isEmpty() && type != other.type) return false
        if (!locationDefinition.implies(other.locationDefinition)) return false
        if (!organizationSpec.implies(other.organizationSpec)) return false
        if (other.relationshipDefinitions && !other.relationshipDefinitions.every {ord ->
            relationshipDefinitions.any {rd -> rd.implies(ord)}
        }) return false
        return true;
    }

    MatchResult fullMatch(Bean bean, InformationAct informationAct) {
        return null;  // TODO
    }


}