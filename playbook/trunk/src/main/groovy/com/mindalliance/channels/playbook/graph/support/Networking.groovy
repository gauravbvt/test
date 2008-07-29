package com.mindalliance.channels.playbook.graph.support

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.ifm.project.resources.Organization
import com.mindalliance.channels.playbook.ifm.project.resources.Resource
import com.mindalliance.channels.playbook.ref.impl.ComputedRef

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 28, 2008
 * Time: 1:31:45 PM
 */
// TODO - implements Referenceable -- update Ref/Referenceable framework
class Networking {  // scope is the fromResource's project

    Ref fromResource
    Ref toResource

    private int cachedSize = -1

    static Networking makeNetworking(Ref fromResource, Ref toResource) {
        return new Networking(fromResource: fromResource, toResource: toResource)
    }

    Ref getReference() {
       return new ComputedRef("${this.class},makeNetworking,$fromResource,$toResource")
    }

    List<Ref> getRelationships() {
        return relationships = (List<Ref>) fromResource.project.relationships.findAll {rel -> rel.fromAgent == fromResource && rel.toAgent == toResource }
    }

    List<Ref> getAgreements() {
        return (List<Ref>) Query.execute(fromResource.project, "findAllAgreementsBetween", fromResource, toResource)
    }

    List<Ref> getFlowActs() {
        return (List<Ref>) Query.execute(fromResource.project, "findAllFlowActsBetween", fromResource, toResource)
    }

    boolean hasAccess() {
        return toResource.access.any {protocol ->
            protocol.contacts.any {agentSpec -> agentSpec.matches(fromResource, null) }
        }
    }

    boolean hasJobWith() {
        return hasJobWith(fromResource, toResource)
    }

    boolean isOrganizationOf() {
        return hasJobWith(toResource, fromResource)
    }

    private boolean hasJobWith(Ref resource, Ref organization) {
        Resource res = (Resource) resource.deref()
        if (res.isAnOrganization()) {
            return ((Organization) res).hasResource(resource)
        }
        else {
            return false
        }
    }

    int getSize() {
        if (cachedSize == -1) {
            int count = 0
            count += this.relationships.size()
            count += this.agreements.size()
            count += this.flowActs.size()
            if (hasAccess()) count++
            if (hasJobWith()) count++
            if (isOrganizationOf()) count++
            cachedSize = count
        }
        return cachedSize
    }

}