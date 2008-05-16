package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.query.Query

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 9:46:51 PM
 */
class Group extends PlaybookElement implements Agent {

    static public final List<String> resourceKinds = ['Organization', 'Position', 'System']

    String name = ''
    String description = ''
    List<String> kinds = [] // restricted to these kinds - empty means any kind
    List<Ref> organizationTypes = [] // must be in organizations or be organizations of any of these types - empty means of any type
    List<Ref> roles = [] // must play any of these roles - empty means any role
    List<String> relationshipNames = [] // must have any of these relationships with related agent -- any if empty
    Ref relatedResource // relationships to this resource -- required if relationshipNames set
    Location location = new Location() // must have a jurisdiction or location within this one - null means any

    @Override
    List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['resourceKinds'])
    }

    // queries


    // end queries

    List<Ref> getResourcesAt(InformationAct act) {
        List<Ref> resources = []
        resources.addAll((List<Ref>) Query.execute(getProject(), "findAllResourcesOfKinds", kinds))
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
        if (relationshipNames) {
            resources = resources.findAll {res ->
                relationshipNames.any {relName ->
                    (boolean)Query.execute(res, "hasRelationship", relName, relatedResource, act)
                }
            }
        }
        if (location) {
            resources = resources.findAll {res ->
                res.isLocatedWithin(location)
            }
        }
        return resources
    }
}