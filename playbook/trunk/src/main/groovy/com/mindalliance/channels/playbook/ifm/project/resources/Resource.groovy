package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.project.resources.System
import com.mindalliance.channels.playbook.ifm.Locatable
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.playbook.Event
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.ifm.Responsibility

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 11:16:29 AM
 */
abstract class Resource extends ProjectElement implements Agent, Locatable {

    String name = ''
    String description = ''
    List<ContactInfo> contactInfos = []
    List<Ref> roles = []
    Location location = new Location()
    boolean effective = true // whether the resource is operational in real life

    @Override
    List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['organizationResource', 'organizationElement', 'responsibilities'])
    }

    boolean isResourceElement() {
        return true
    }

    public boolean isTeam() {
        return false;
    }

    public boolean isGroup() {
        return false;
    }

    boolean isOrganizationResource() {
        return false
    }

    boolean isOrganizationElement() {
        return false
    }

    List<Responsibility> getResponsibilities() {
        // TODO
    }

    String toString() { name }

    /**
     * Return subclass that a project user may want to create.
     */
    static List<Class<?>> contentClasses() {
        [
                Organization.class, Person.class, System.class,
                Position.class
        ]
    }

    @Override
    void beforeStore() {
        super.beforeStore()
        if (location) location.detach()
    }

    @Override
    void changed(String propName) {
        if (propName == 'location') {
            location.detach()
        }
        super.changed(propName)
    }

    public List<Ref> getResourcesAt(Event event) {
        return [this.reference]
    }

    Location getLocation() {
        return location
    }

    List<Ref> allAgreements() {
        return Project.current().findAllAgreementsOf(this.reference)
    }

    boolean hasRole(Ref role) {
        return (this.roles.any {resRole -> resRole.implies(role) })
    }

    boolean isLocatedWithin(Location loc) {
        return this.location.isWithin(loc)
    }

    // Queries

    boolean hasRelationship(String relName, Ref otherResource, Event event) {
        boolean related = false
        // Look in project
        related = related || getProject().relationships.any {rel ->
            rel.fromAgent == this && (!otherResource || rel.toAgent == otherResource) && rel.name == relName
        }
        // Look at associations before act
        related = related || event.playbook.createsRelationshipBefore(new Relationship(fromAgent:this.reference, name:relName, toAgent:otherResource), event)
        return related
    }

    List<Ref> findAllInformationActsForResource() {
        List<Ref> acts = []
        assert this.project
        this.project.playbooks.each {pb ->
            pb.informationActs.each {act ->
                if (act.actorAgent == this.reference) { acts.add(act) }
                if (act.isFlowAct() && act.targetAgent == this.reference) { acts.add(act) }
                // Don't include acts where resource is implied by act's agent
            }
        }
        return acts
    }
    // end queries


}