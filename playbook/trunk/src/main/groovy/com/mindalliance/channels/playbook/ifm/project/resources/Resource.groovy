package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.project.resources.System
import com.mindalliance.channels.playbook.ifm.Locatable
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.Describable
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 11:16:29 AM
 */
class Resource extends ProjectElement implements Agent, Locatable {

    String name = ''
    String description = ''
    List<ContactInfo> contactInfos = []
    List<Ref> roles = []
    Location location = new Location()
    boolean effective = true // whether the resource is operational in real life

    @Override
    List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['agreements'])
    }

    boolean isResource() {
        return true
    }

    boolean isOrganizationResource() {
        return false
    }

    boolean isOrganization() {
        return false
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

    public List<Ref> getResourcesAt(InformationAct act) {
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

    boolean hasRelationship(String relName, Ref otherResource, InformationAct act) {
        boolean related = false
        // Look in project
        related = related || getProject().relationships.any {rel ->
            rel.fromAgent == this && (!otherResource || rel.toAgent == otherResource) && rel.name == relName
        }
        // Look at associations before act
        related = related || act.getPlaybook().findAllInformationActs("Association").any {association ->
            act.isAfter(association) &&
            association.relationships.any {rel ->
                rel.fromAgent == this && (!otherResource || rel.toAgent == otherResource) && rel.name == relName
            }
        }
        return related
    }
    // end queries


}