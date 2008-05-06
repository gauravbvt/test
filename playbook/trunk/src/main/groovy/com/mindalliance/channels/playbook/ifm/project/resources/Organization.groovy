package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.query.Query

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 11:24:14 AM
 */
class Organization extends Resource {

    Ref parent // // set only via organization.add|removeSubOrganization(...)
    List<Ref> subOrganizations = []
    List<Ref> positions = []
    List<Ref> systems = []
    Location jurisdiction
    List<Ref> organizationTypes = []

    void beforeStore() {
        super.beforeStore()
        if (jurisdiction) jurisdiction.detach()
    }

    Referenceable doAddToField(String field, Object object) {
        switch (object.deref()) {
            case Organization.class: object.parent = this.reference;  super.doAddToField(field, object); break
            case Position.class:
            case System.class: object.organization = this.reference; super.doAddToField(field, object); break
            default: super.doAddToField(field, object);
        }
    }

    Referenceable doRemoveFromField(String field, Object object) {
        switch (object.deref()) {
            case Organization.class: object.parent = null;  super.doRemoveFromField(field, object); break
            case Position.class:
            case System.class: object.organization = null; super.doRemoveFromField(field, object); break
            default: super.doRemoveFromField(field, object);
        }
    }

    List<Ref> allParents() {
        List<Ref> parents = []
        if (parent) {
            parents.addAll(parent.allParents())
            parents.add(parent)
        }
        return parents
    }

    // Queries

    List<Ref> findAllSubOrganizations() {
        List<Ref> allSubs = []
        subOrganizations.each {sub -> sub.addAll(sub.findAllSubOrganizations())}
        allSubs.addAll(subOrganizations)
        return allSubs
    }

    List<Ref> findAllPositions() {
        List<Ref> allPositions = []
        List<Ref> allSubs = findAllSubOrganizations()
        allSubs.each {sub -> allPositions.addAll(sub.positions)}
        allSubs.addAll(positions)
        return allSubs
    }




}