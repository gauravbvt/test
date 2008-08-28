package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.project.resources.System
import com.mindalliance.channels.playbook.ifm.Locatable
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.ifm.Responsibility
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.ifm.sharing.SharingProtocol
import com.mindalliance.channels.playbook.ifm.project.ProjectElement

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 11:16:29 AM
 */
abstract class Resource extends ProjectElement implements Locatable {

    String name = ''
    String description = ''
    List<ContactInfo> contactInfos = []
    Location location = new Location()
    List<SharingProtocol> access = []
    boolean effective = true // whether the resource is operational in real life

    @Override
    List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['organizationResource', 'organizationElement', 'responsibilities',
                                  'resourceElement', 'anOrganization', 'anIndividual', 'responsible', 'agent', 'aPerson', 'aJob', 'aSystem'])
    }

    Set hiddenProperties() {
        return (super.hiddenProperties() + ['organizationResource', 'organizationElement']) as Set
    }


    Set keyProperties() {
        return (super.keyProperties() + ['name', 'description']) as Set
    }

    boolean isAnOrganization() {
        return false
    }

    boolean isAPerson() {
        return false
    }

    boolean isASystem() {
        return false
    }

    boolean isAJob() {
        return false
    }

    boolean isOrganizationResource() {
        return false
    }

    boolean isOrganizationElement() {
        return false
    }

    boolean hasJurisdiction() {
        return false
    }

    boolean isAnIndividual() {
        return false
    }

    boolean isAgent() {   // can be an agent
        return false
    }

    boolean hasLocation() {
        return location.isDefined()
    }


    List<Responsibility> getResponsibilities() {
        List<Responsibility> allResponsibilities = []
        getRoles().each {role -> if (role as boolean) allResponsibilities.addAll(role.responsibilities)}
        return allResponsibilities
    }


    String toString() { name ?: "Unnamed" }

    /**
     * Return subclass that a project user may want to create.
     */
    static List<Class<?>> contentClasses() {
        return (List<Class<?>>)[
                Organization.class, Person.class, System.class,
                Position.class
        ]
    }

    @Override
    void beforeStore(ApplicationMemory memory) {
        super.beforeStore(memory)
        if (location) location.detach()
    }

    @Override
    void changed(String propName) {
        if (propName == 'location') {
            location.detach()
        }
        super.changed(propName)
    }

 /*   public List<Ref> getResourcesAt(Ref event) {
        return [this.reference]
    }
*/
    Location getLocation() {
        return location
    }

    List<Ref> allAgreements() {
        return Project.current().findAllAgreementsOf(this.reference)
    }


    boolean isLocatedWithin(Location loc) {
        return this.location.isWithin(loc)
    }

    boolean hasAccessTo(Ref resource) {
        boolean result = access.any {protocol ->
            protocol.contacts.any {resSpec -> resSpec.matches(resource, null) }
        }
        return result
    }

    boolean hasJobWith(Ref org) {
        if (isAnIndividual() && org as boolean && org.isAnOrganization()) {
            return org.employs(this.reference)
        }
        else {
            return false
        }
    }


    // Queries

 /*   boolean hasRelationship(String relName, Ref otherResource, Ref event) {
        // Look in project
        related = getProject().relationships.any {rel ->
            rel.fromResource == this && (!otherResource || rel.toResource == otherResource) && rel.name == relName
        }
        // Look at associations before act
        related = related || event.playbook.createsRelationshipBefore(new Relationship(fromResource:this.reference, name:relName, toResource:otherResource), event)
        return related
    }*/

    List<Ref> findAllInformationActsForResource() {
        List<Ref> acts = []
        assert this.project
        this.project.playbooks.each {pb ->
            pb.informationActs.each {act ->
                if (act.actors.contains(this.reference)) { acts.add(act) }
                if (act.isFlowAct() && act.targetAgent == this.reference) { acts.add(act) }
            }
        }
        return acts
    }

    // end queries


}