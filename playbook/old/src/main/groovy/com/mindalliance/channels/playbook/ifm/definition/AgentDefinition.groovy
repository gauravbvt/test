package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.resources.Organization
import com.mindalliance.channels.playbook.ifm.project.resources.Job
import com.mindalliance.channels.playbook.ifm.project.resources.Position
import com.mindalliance.channels.playbook.ifm.playbook.Group

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 18, 2008
 * Time: 4:19:50 PM
 */
class AgentDefinition extends Definition {

    ResourceSpecification resourceSpec = new ResourceSpecification() // what kind of resource? -- applies to Individual in Job
    List<Ref> roles = [] // ANDed  -- with what roles? -- if list empty, don't care about roles
    OrganizationSpecification organizationSpec = new OrganizationSpecification()  // working for what kind of organization?
    LocationDefinition jurisdictionDefinition = new LocationDefinition() // with what jurisdiction?


    public Class<? extends Bean> getMatchingDomainClass() {
        return Agent.class;
    }

    public boolean matchesAll() {
        return roles.isEmpty() && resourceSpec.matchesAll() && jurisdictionDefinition.matchesAll() && organizationSpec.matchesAll()
    }

    public MatchResult match(Bean bean, InformationAct informationAct) {
        Agent agent = (Agent) Bean
        switch (agent) {
            case Job.class: return matchJob((Job)bean, informationAct)
            case Position.class: return matchPosition((Position)bean, informationAct)
            case Organization.class: return matchOrganization((Organization)bean, informationAct)
            case Group.class: return matchGroup((Group)bean, informationAct)
        }
    }

    private MatchResult matchJob(Job job, InformationAct informationAct) {
        if (!job.individual as boolean) return new MatchResult(matched: false, failures: ["Individual for $job is not set"])
        if (!resourceSpec.matchesAll()) {
            if (!resourceSpec.matches(job.individual.deref(), informationAct)) {
                return new MatchResult(matched: false, failures: ["Individual in $job does not match as a resource"])
            }
        }
        if (!job.position as boolean) return new MatchResult(matched: false, failures: ["Position for $job is not set"])
        return matchPosition((Position)job.position.deref(), informationAct)
    }


    private MatchResult matchPosition(Position position, InformationAct informationAct) {
        if (!roles.isEmpty()) {
            if (!roles.every {srole -> position.roles.any {role -> role.implies(srole)}}) {  // if not all of the prescribed roles is matched at least one of the job's roles, then fail
                return new MatchResult(matched: false, failures: ["$position does not match all specified roles"])
            }
        }
        if (!organizationSpec.matchesAll()) {
            Ref organization = position.organization
            if (!organization as boolean || !organizationSpec.matches(organization.deref(), informationAct)) {
                return new MatchResult(matched: false, failures: ["$position does not match specified organization"])
            }

        }
        if (!jurisdictionDefinition.matches(position.jurisdiction, informationAct)) {
            return new MatchResult(matched: false, failures: ["Jurisdiction of $position does not match"])
        }
        return new MatchResult(matched: true)
    }

    private MatchResult matchOrganization(Organization organization, InformationAct informationAct) {
        // All all the specified roles present in the organization?
        if (!roles.isEmpty()) {
             if (!roles.every {srole -> organization.roles.any {role -> role.implies(srole)}}) {  // if not all of the prescribed roles is matched at least one of the job's roles, then fail
                 return new MatchResult(matched: false, failures: ["$organization does not match all specified roles"])
             }
         }
        // Does the organiztion match the organization specification?
        if (!organizationSpec.matchesAll()) {
             if (!organizationSpec.matches(organization, informationAct)) {
                 return new MatchResult(matched: false, failures: ["$organization does not match specified organization"])
             }
         }
        // Does the organization's jurisdiction match?
        if (!jurisdictionDefinition.matches(organization.jurisdiction, informationAct)) {
            return new MatchResult(matched: false, failures: ["Jurisdiction of $organization does not match"])
        }
        return new MatchResult(matched: true)
    }

    private MatchResult matchGroup(Group group, InformationAct informationAct) {
        // Does the group's AgentSpecification infer this one?
        if (!group.agentSpec.implies(this)) {
            return new MatchResult(matched: false, failures: ["$group specification does not imply this"])
        }
        return new MatchResult(matched: true)
    }

    public MatchResult fullMatch(Bean bean, InformationAct informationAct) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean implies(MatchingDomain matchingDomain) {
        AgentDefinition other = (AgentDefinition) matchingDomain
        if (other.matchesAll()) return true
        // all roles in others implied by a role in this
        if (other.roles && !other.roles.every {orl -> roles.any {rl -> rl.implies(orl)}}) return false
        if (!resourceSpec.implies(other.resourceSpec)) return false
        if (!organizationSpec.implies(other.organizationSpec)) return false
        if (!jurisdictionDefinition.implies(other.jurisdictionDefinition)) return false
        return true;
    }

}