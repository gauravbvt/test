package com.mindalliance.channels.playbook.ifm.project

import com.mindalliance.channels.playbook.ifm.Described
import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ifm.project.environment.SharingAgreement
import com.mindalliance.channels.playbook.ifm.project.environment.Policy
import com.mindalliance.channels.playbook.ifm.project.environment.Place
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.support.PlaybookSession
import com.mindalliance.channels.playbook.support.RefUtils
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.ifm.project.resources.Organization
import com.mindalliance.channels.playbook.ifm.project.resources.Person
import com.mindalliance.channels.playbook.ifm.Named
import com.mindalliance.channels.playbook.ifm.project.resources.Team

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2008
 * Time: 2:10:46 PM
 */
class Project extends IfmElement implements Named, Described {

    String name = 'Unnamed'
    String description = ''
    List<Ref> participations = []
    List<Ref> persons = []
    List<Ref> organizations = []
    List<Ref> teams = []
    List<Ref> places = []
    List<Ref> relationships = []
    List<Ref> policies = []
    List<Ref> sharingAgreements = []    // TODO gc sharingAgreements with null source or recipient (i.e. invalid)
    List<Ref> playbooks = []
    List<Ref> models = []
    List<Ref> analysisElements = []

    static Ref current() {
        PlaybookSession session = (PlaybookSession) Session.get()
        return session.project
    }

    String toString() { name }

    Referenceable doAddToField(String field, Object object) {
        object.project = this.reference
        switch (object.deref()) {
            case Policy: super.doAddToField("policies", object); break;
            default: super.doAddToField(field, object);
        }
    }

    Referenceable doRemoveFromField(String field, Object object) {
        switch (object.deref()) {
            case Policy: super.doRemoveFromField("policies", object); break;
            default: super.doRemoveFromField(field, object);
        }
    }

    // Queries

    Ref findModelNamed(String name) {
        return (Ref)models.find {it.name == name}
    }

    List<Ref> findAllResources() {
        return findAllResourcesExcept(null)
    }

    Ref findPersonNamed(String name) {
       return (Ref)persons.find {it.name == name}
    }

    Ref findOrganizationNamed(String name) {
       return (Ref)organizations.find {it.name == name}
    }

    List<Ref> findAllResourcesExcept(Ref resource) {
        List<Ref> resources = []
        resources.addAll(persons.findAll {res -> res != resource })
        resources.addAll(organizations.findAll {res -> res != resource })
        resources.addAll(teams.findAll {res -> res != resource })
        organizations.each {org ->
            resources.addAll(org.systems.findAll {res -> res != resource })
            resources.addAll(org.positions.findAll {res -> res != resource })
        }
        return resources
    }

    List<Ref> findAllAgents() {
        return findAllResources()
    }

    List<Ref> findAllAgentsExcept(def holder, String propPath) {
        Ref party = RefUtils.get(holder, propPath)
        return findAllResourcesExcept(party)
    }

    Ref findPlaybookNamed(String name) {
       return (Ref)playbooks.find {pb ->
            pb.name == name
        }
    }

    Ref findParticipation(Ref user) {
        Ref p = (Ref) participations.find {p -> p.user == user }
        return p
    }


    List<String> findAllPlaceNames() {
        return places.collect {it.name}
    }

    Ref findPlaceNamed(String placeName) {
        Ref namedPlace = (Ref) places.find {place -> place.name == placeName }
        return namedPlace
    }

    boolean atleastOnePlaceTypeDefined() {
        Ref model = (Ref) models.find {model ->
            model.placeTypes.size() > 0
        }
        return model != null
    }

    List<Ref> findAllTypes(String typeType) {
        List<Ref> types = []
        models.each {model ->
            types.addAll(model.findAllTypes(typeType))
        }
        return types
    }

    List<String> findAllOtherTypeNames(Ref elementType) {
        List<Ref> allTypes = findAllTypes(elementType.type)
        List<String> otherNames = []
        allTypes.each {type ->
            if (type != elementType) otherNames.add(type.name)
        }
        return otherNames
    }

    Ref findElementTypeNamed(String typeType, String elementTypeName) {
        String propName = RefUtils.decapitalize("${typeType}s")
        Ref namedType = null
        models.any {model ->
            List list = model."$propName"
            list.any {type ->
                String typeName = type.name
                if (typeName.equalsIgnoreCase(elementTypeName)) {
                    namedType = type
                }
                namedType
            }
            namedType
        }
        return namedType
    }

    List<Ref> findAllTypesNarrowing(Ref elementType) {
        List<Ref> types = []
        findAllTypes(elementType.type).each {type ->
            if (type.narrows(elementType)) types.add(type)
        }
        return types
    }

    List<Ref> findAllTypesNarrowingAny(List<Ref> elementTypes) {
        Set<Ref> types = new HashSet<Ref>()
        elementTypes.each {elementType ->
            types.addAll(this.findAllTypesNarrowing(elementType))
        }
        return types as List
    }

    List<Ref> findPlaceTypesNarrowing(Ref placeType) {
        List<Ref> narrowing = []
        models.each {model ->
            model.placeTypes.each {pt ->
                if (placeType == null && pt.parent == null) { // top level place types narrow undefined place type
                    narrowing.add(pt)
                }
                else if (pt.parent == placeType) {
                    narrowing.add(pt)
                }
            }
        }
        return narrowing
    }

/*
    List<Ref> findAllApplicableRelationshipTypes(Ref fromResource, Ref toResource) {
        List<Ref> relTypes = []
        models.each { model ->
            relTypes.addAll(
                    model.relationshipTypes.findAll {rt ->
                        rt.matchesFrom(fromResource) && rt.matchesTo(toResource)
            })
        }
        return relTypes
    }
*/

    List<Ref> findAllAgreementsOf(Ref resource) {
        List<Ref> ags = sharingAgreements.findAll {agreement ->
            agreement.fromResource == resource
        }
        return ags ?: []
    }

    // Find all organizations that are not
    // - the organization
    // -  a sub organization of some organization
    // - a parent organization (transitively) of the organization
    List<Ref> findCandidateSubOrganizationsFor(Ref organization) {
        List<Ref> candidates = organizations.findAll {org ->
            org != organization &&
                    !org.parent &&
                    !organization.allParents().contains(org)
        }
        return candidates
    }

    List<Ref> findAllPositionsAnywhere() {
        List<Ref> allPositions = []
        organizations.each {org -> allPositions.addAll(org.positions) }
        return allPositions
    }

    List<Ref> findAllRelationshipsOf(Ref resource) {
        return relationships.findAll {rel ->
            rel.fromAgent == resource || rel.toAgent == resource}
    }

    List<Ref> findAgreementsWhereSource(Ref resource) {
        return sharingAgreements.findAll {agr -> agr.source == resource}
    }

    List<Ref> findAgreementsWhereRecipient(Ref resource) {
        return sharingAgreements.findAll {agr -> agr.recipient == resource}
    }

    List<Ref> findAllJurisdictionables() {
        return (List<Ref>)findAllAgents().findAll {agent -> agent.hasJurisdiction()}
    }

    List<Ref> findAllPlacesOfTypeImplying(Ref placeType) {
        return (List<Ref>)places.findAll {place -> place.placeType as boolean && place.placeType.implies(placeType)}
    }

    List<Ref> findAllAgentsLocatedInPlacesOfTypeImplying(Ref placeType) {
        return (List<Ref>)findAllAgents().findAll {agent ->
            agent.hasLocation() && agent.location.isAPlace() && agent.location.place.placeType as boolean && agent.location.place.placeType.implies(placeType)
        }
    }

    List<Ref> findAllAgentsWithJurisdictionsInPlacesOfTypeImplying(Ref placeType) {
        return (List<Ref>)findAllAgents().findAll {agent ->
            agent.hasJurisdiction() && agent.jurisdiction.isAPlace() && agent.jurisdiction.place.placeType as boolean && agent.jurisdiction.place.placeType.implies(placeType)
        }
    }

    List<Ref> findAllPlacesInAreasOfTypeImplying(Ref areaType) {
        return (List<Ref>)places.findAll {place -> (geoLoc = place.findGeoLocation()) && geoLoc.isDefined() && geoLoc.areaType.implies(areaType) }
    }

    List<Ref> findAllAgentsLocatedInAreasOfTypeImplying(Ref areaType) {
        return (List<Ref>)findAllAgents().findAll {agent -> agent.hasLocation() && (geoLoc = agent.location.effectiveGeoLocation) && geoLoc.isDefined() && geoLoc.areaType.implies(areaType)}
    }

    List<Ref> findAllAgentsWithJurisdictionsInAreasOfTypeImplying(Ref areaType) {
        return (List<Ref>)findAllAgents().findAll {agent -> agent.hasJurisdiction() && (geoLoc = agent.jurisdiction.effectiveGeoLocation) && geoLoc.isDefined() && geoLoc.areaType.implies(areaType)}
    }

    // End queries

    Boolean isParticipant(Ref user) {
        return findParticipation(user) != null;
    }

    Boolean isManager(Ref user) {
        Ref ref = findParticipation(user)
        return ref != null && ref.manager;
    }

    /**
     * Return project contents that a participant can add.
     */
    static List<Class<?>> contentClasses() {
        // When changing this method, don't forget to update the next one...
        List<Class<?>> result = new ArrayList<Class<?>>()
        result.addAll([Organization.class])
        result.addAll([Team.class])
        result.addAll([Place.class])
        result.addAll([Playbook.class])
        result.addAll([Person.class])
        result.addAll([Policy.class])
        result.addAll([Relationship.class])
        result.addAll([SharingAgreement.class])
        result.addAll(Playbook.contentClasses())
        return result
    }

    void addContents(List<Ref> result) {
        playbooks.each { it.addContents(result) }
        result.addAll(analysisElements)
        result.addAll(organizations)
        result.addAll(teams)
        result.addAll(persons)
        result.addAll(places)
        result.addAll(playbooks)
        result.addAll(policies)
        result.addAll(relationships)
        result.addAll(sharingAgreements)
    }

    /**
     * Return system objects that a project manager can add.
     */
    static List<Class<?>> managerClasses() {
        [Project.class]
    }

    void addManagerContents(List<Ref> result) {
        // Projects are added in UserScope.getContents()
    }

}