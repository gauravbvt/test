package com.mindalliance.channels.playbook.ifm.spec

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.playbook.Event
import com.mindalliance.channels.playbook.ifm.spec.Spec
import com.mindalliance.channels.playbook.ifm.spec.RelationshipSpec
import com.mindalliance.channels.playbook.ifm.info.Location

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
    Location location = new Location() // if defined, agent has location or jurisdiction within

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
        return resourceSpec.isDefined() || relationshipSpec.isDefined()
    }

    public boolean matches(Ref element) {
        return resourceSpec.matches(element) && false // TODO
    }

    public boolean narrows(Spec spec) {
        return resourceSpec.narrows(spec) && false;  // TODO
    }
}