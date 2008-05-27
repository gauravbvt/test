package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.playbook.Event

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 20, 2008
 * Time: 2:43:18 PM
 */
class AgentSpec extends BeanImpl implements Spec {

    List<Ref> roles = [] // agent plays any of these roles, or any role at all if empty list
    List<Ref> organizationTypes = [] // agent is or is on any of these organization types, or any at all if empty list
    RelationshipSpec relationshipSpec  = new RelationshipSpec() // agent has such specified relationships, or any at all if empty
    Location location = new Location() // if defined, agent has location or jurisdiction within

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['defined'])
    }

    List<Ref> getResourcesAt(Event event) {
        List<Ref> resources = []
        resources.addAll((List<Ref>) Query.execute(getProject(), "findAllResources"))
        if (organizationTypes) { // Filter resources on organization types
            resources = resources.findAll {res ->
                (res.isOrganizationResource() &&
                        res.organization.organizationTypes.any {ot ->
                            organizationTypes.any {spec -> ot.implies(spec)}
                        }
                ) ||
                (res.isOrganization() &&
                        res.organizationTypes.any {ot ->
                            organizationTypes.any {spec -> ot.implies(spec)}
                        })
            }
        }
        if(roles) { // Filter on roles
           resources = resources.findAll {res ->
                roles.any {spec -> res.hasRole(spec)}
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

    private String orgTypesSummary() {
        String summary
        if (organizationTypes.isEmpty()) {
            summary = "of any type"
        }
        else {
            summary = "classified as "
            organizationTypes.each {type -> summary += "${type.name} or "}
            summary = summary.substring(0, summary.size()- 4)
        }
        return summary
    }

    public boolean isDefined() {
        return !roles.isEmpty() || !organizationTypes.isEmpty() || relationshipSpec.isDefined()
    }

    public boolean matches(Ref element) {
        return false;  // TODO
    }

    public boolean narrows(Spec spec) {
        return false;  // TODO
    }
}