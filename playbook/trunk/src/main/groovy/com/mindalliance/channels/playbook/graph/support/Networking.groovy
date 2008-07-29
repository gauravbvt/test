package com.mindalliance.channels.playbook.graph.support

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.ifm.project.resources.Organization
import com.mindalliance.channels.playbook.ifm.project.resources.Resource
import com.mindalliance.channels.playbook.ref.impl.ComputedRef
import com.mindalliance.channels.playbook.ref.impl.AbstractReferenceableImpl

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 28, 2008
 * Time: 1:31:45 PM
 */
class Networking extends AbstractReferenceableImpl {  // scope is the fromResource's project

    Ref fromResource
    Ref toResource

    private int cachedSize = -1

    static Networking makeNetworking(Ref fromResource, Ref toResource) {
        return new Networking(fromResource: fromResource, toResource: toResource)
    }

    Ref getReference() {
       return new ComputedRef(getId())
    }

    List<Ref> getRelationships() {
        return (List<Ref>) fromResource.project.relationships.findAll {rel -> rel.fromAgent == fromResource && rel.toAgent == toResource }
    }

    List<Ref> getAgreements() {
        return (List<Ref>) Query.execute(fromResource.project, "findAllAgreementsBetween", fromResource, toResource)
    }

    List<Ref> getFlowActs() {
        return (List<Ref>) Query.execute(fromResource.project, "findAllFlowActsBetween", fromResource, toResource)
    }

    boolean hasAccess() {
        boolean result = toResource.access.any {protocol ->
            protocol.contacts.any {agentSpec -> agentSpec.matches(fromResource, null) }
        }
        return result
    }

    boolean hasJobWith() {
        return hasJobWith(fromResource, toResource)
    }

    private boolean hasJobWith(Ref res1, Ref res2) {
        Resource resource2 = (Resource) res2.deref()
        if (resource2.isAnOrganization()) {
            return ((Organization) resource2).hasResource(res1)
        }
        else {
            return false
        }
    }

    int size() {
        if (cachedSize == -1) {
            int count = 0
            List<Ref> rels = this.relationships
            count += rels.size()
            List<Ref> agrs = this.agreements
            count += agrs.size()
            List<Ref> fas = this.flowActs
            count += fas.size()
            if (hasAccess()) count++
            if (hasJobWith()) count++
            cachedSize = count
        }
        return cachedSize
    }

   public boolean isConstant() {
        return true;
    }

    public String makeLabel(int maxWidth) {
        return "${size()}";
    }

    public String about() {
        return "Networking between ${fromResource.about()} and ${toResource.about()}";
    }

    public String getId() {
        return "${this.class.name},makeNetworking,$fromResource,$toResource";
    }
}