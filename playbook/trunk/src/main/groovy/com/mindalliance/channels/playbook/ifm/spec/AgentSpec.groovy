package com.mindalliance.channels.playbook.ifm.spec

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.playbook.Event
import com.mindalliance.channels.playbook.ifm.spec.Spec
import com.mindalliance.channels.playbook.ifm.spec.RelationshipSpec
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 20, 2008
 * Time: 2:43:18 PM
 */
class AgentSpec extends SpecImpl {

    ResourceSpec resourceSpec = new ResourceSpec()
    RelationshipSpec relationshipSpec  = new RelationshipSpec() // agent has such specified relationships, or any at all if empty
    Location location = new Location() // if defined, matching agent must be currently located within
    Location jurisdiction = new Location() // if defined, matching agent's jurisdiction must be located within

    List<Ref> getResourcesAt(Event event) {
        List<Ref> resources = []
        resources.addAll((List<Ref>) Query.execute(getProject(), "findAllResources"))
        if (organizationTypes) { // Filter resources on organization types
            resources = resources.findAll {res ->
                (res.isOrganizationResource() &&
                        res.organization.organizationTypes.any {ot ->
                            resourceSpec.organizationTypes.any {spec -> ot.implies(spec)}
                        }
                ) ||
                (res.isOrganizationElement() &&
                        res.organizationTypes.any {ot ->
                            organizationTypes.any {spec -> ot.implies(spec)}
                        })
            }
        }
        if(roles) { // Filter on roles
           resources = resources.findAll {res ->
                resourceSpec.roles.any {spec -> res.hasRole(spec)}
           }
        }
        if (relationshipSpec.isDefined()) {
            resources = resources.findAll {res ->
                relationshipSpec.matchedBy(res, event)
            }
        }
        if (location) {
            resources = resources.findAll {res ->
                res.isLocatedWithin(location)
            }
        }
        return resources
    }

   public boolean isDefined() {
        return resourceSpec.isDefined() || relationshipSpec.isDefined() || location.isDefined() || jurisdiction.isDefined()
    }

    // Matching of an agent in context against a specification
    boolean doesMatchAsOf(IfmElement agent, InformationAct act) {
        if (!agent instanceof Agent) throw new IllegalArgumentException("Can not match to an $agent")
        // First match agent as a resource
        if (!resourceSpec.matches(agent)) return false
        // Match against relationships
        if (relationshipSpec.isDefined()) {
            List<Ref> relationships = (List<Ref>)Query.execute(act.playbook, "findAllRelationshipsOfAgentAsOf", agent, act)  // TODO
            if (!relationships.any {rel -> relationshipSpec.matches(rel.deref())}) return false
        }
        // Match against location
        if (location)
        return true
    }

    public boolean narrows(Spec spec) {
        return false  // TODO
    }
}