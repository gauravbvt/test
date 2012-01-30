package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.project.environment.Policy

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 11:24:14 AM
 */
class Organization extends Resource implements Agent {   // a company, agency, team, matrix etc.

    List<Ref> subOrganizations = []
    List<Ref> positions = []
    List<Ref> systems = []
    List<Ref> policies = []
    Location jurisdiction = new Location()
    List<Ref> organizationTypes = []

    void addContents(List<Ref> result) {
        result.addAll(positions)
        result.addAll(systems)
        result.addAll(policies)
    }

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['resources', 'parents', 'ancestors', 'parent', 'roles'])
    }


    protected List<String> childProperties() {
        return (List<String>)(super.childProperties() + ['positions', 'systems', 'policies'])
    }

    public Set<Class<?>> childClasses() {
        return super.childClasses() + [
               Position.class, System.class, Policy.class
               ] as Set<Class<?>>
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

    boolean isAnOrganization() {
         return true
     }

    boolean isAgent() {
        return true
    }

    boolean hasResource(Ref resource) {
        if (this.resources.contains(resource)) return true
        return subOrganizations.any {org -> org.hasResource(resource)}
    }

    boolean isLocatedWithin(Location loc) {
        return super.isLocatedWithin(loc) || jurisdiction.isWithin(loc)
    }

    Ref getParent() {           // TODO -KLUDGE until OrganizationFilter updated to multi-parents
        List refs = parents
        return refs ? (Ref)refs[0] : null
    }

    boolean isPartOf( Ref org ) {
        List<Ref> parents = getParents()
        if ( parents.isEmpty() && org == null )
            return true
        else if ( org == null )
            return false
        if ( parents.contains( org ) )
            return true
        for( Ref p: parents )
            if ( p.isPartOf( org ) )
                return true
        return false
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

    List<Ref> getRoles() {
        return positions.roles.flatten()
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
        if (this.positions.any {position -> position as boolean && position.hasRole(role) }) return true
        return false
    }

    boolean employs(Ref individual) {
        return project.jobs.any {job -> positions.contains(job.position) && job.individual == individual}
    }

    // end queries


}