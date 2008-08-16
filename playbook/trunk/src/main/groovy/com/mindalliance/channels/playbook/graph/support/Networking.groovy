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
class Networking extends AbstractReferenceableImpl {  // scope is the resource's project

    Ref resource
    Ref otherResource

    private int cachedSize = -1

    static Networking makeNetworking(Ref resource, Ref otherResource) {
        return new Networking(resource: resource, otherResource: otherResource)
    }

    Ref getReference() {
       return new ComputedRef(getId())
    }

    List<Ref> getRelationships() {
        return (List<Ref>) resource.project.relationships.findAll {rel -> (rel.fromAgent == resource && rel.toAgent == otherResource) ||
                                                                              (rel.fromAgent == otherResource && rel.toAgent == resource) }
    }

    List<Ref> getAgreements() {
        List<Ref> agreements = (List<Ref>)Query.execute(resource.project, "findAllAgreementsBetween", resource, otherResource)
        agreements += (List<Ref>)Query.execute(resource.project, "findAllAgreementsBetween", otherResource, resource)
        return agreements
    }

    List<Ref> getFlowActs() {
        List<Ref> acts = (List<Ref>) Query.execute(resource.project, "findAllFlowActsBetween", resource, otherResource)
        acts += (List<Ref>) Query.execute(resource.project, "findAllFlowActsBetween", otherResource, resource)
        return acts
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
            if (resource.hasAccessTo(otherResource)) count++
            if (otherResource.hasAccessTo(resource)) count++
            if (resource.hasJobWith(otherResource)) count++
            if (otherResource.hasJobWith(resource)) count++
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
        return "Networking between ${resource.about()} and ${otherResource.about()}";
    }

    public String getId() {
        return "${this.class.name},makeNetworking,$resource,$otherResource";
    }
}