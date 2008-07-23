package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.support.RefUtils
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.query.Query

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 11:24:14 AM
 */
class Organization extends Resource {   // a company, agency, team, matrix etc.

    // Ref parent // // set only via organization.add|removeSubOrganization(...)
    List<Ref> subOrganizations = []
    List<Ref> positions = []
    List<Ref> systems = []
    Location jurisdiction = new Location()
    List<Ref> organizationTypes = []

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['resources', 'parents', 'ancestors', 'parent'])
    }


    void beforeStore(ApplicationMemory memory) {
        super.beforeStore(memory)
        jurisdiction.detach()
    }

    @Override
    void changed(String propName) {
        if (propName == 'jurisdiction') {
            jurisdiction.detach()
        }
        super.changed(propName)
    }

    boolean isOrganizationElement() {
        return true
    }

    boolean isLocatedWithin(Location loc) {
        return super.isLocatedWithin(loc) || jurisdiction.isWithin(loc)
    }

    Ref getParent() {           // TODO -KLUDGE until OrganizationFilter updated to multi-parents
        List<Ref> allParents = getParents()
        return (parents) ? (Ref)parents[0] : null
    }

    List<Ref> getParents() {
        List<Ref> directParents = (List<Ref>)Query.execute(this.project, "findAllParentsOf", this.reference)
        return directParents
    }

    List<Ref> getResources() {
        List<Ref> resources = []
        resources.addAll(positions)
        resources.addAll(systems)
        return resources
    }

    void addElement(IfmElement element) {
        String type = element.type
        String field = "${RefUtils.decapitalize(type)}s"
        doAddToField(field, element)
    }

    Referenceable doAddToField(String field, Object object) {
        Referenceable referenceable = object.deref()
        switch (referenceable) {
            case Position.class:
            case System.class:
                // referenceable.reference.begin()    // TODO -- may fail in multi-user scenario
                referenceable.organization = this.reference
                super.doAddToField(field, object); break
            default: super.doAddToField(field, object);
        }
    }

    Referenceable doRemoveFromField(String field, Object object) {
        Referenceable referenceable = object.deref()
        switch (referenceable) {
            case Position.class:
            case System.class:
                // referenceable.reference.begin()   // TODO -- may fail in multi-user scenario
                referenceable.organization = null
                super.doRemoveFromField(field, object); break
            default: super.doRemoveFromField(field, object);
        }
    }

    List<Ref> getAncestors() {   // meaning ancestors
        List<Ref> ancestry = this.parents
        this.parents.each {ref ->
            if (ref as boolean) ancestry.addAll(ref.ancestors)
        }
        return ancestry
    }

    boolean hasJurisdiction() {
        return jurisdiction.isDefined()
    }

    // Queries

    List<Ref> findAllSubOrganizations() {
        List<Ref> allSubs = []
        subOrganizations.each {sub -> if (sub as boolean) allSubs.addAll(sub.findAllSubOrganizations())}
        allSubs.addAll(subOrganizations)
        return allSubs
    }

    List<Ref> findAllPositions() {
        List<Ref> allPositions = []
        List<Ref> allSubs = findAllSubOrganizations()
        allSubs.each {sub -> if (sub as boolean) allPositions.addAll(sub.positions)}
        allPositions.addAll(positions)
        return allPositions
    }

    boolean hasRole(Ref role) {
        if (super.hasRole(role)) return true
        if (this.positions.any {position -> position as boolean && position.hasRole(role) }) return true
        if (this.systems.any {system -> system as boolean && system.hasRole(role) }) return true
        return false
    }

    // end queries


}