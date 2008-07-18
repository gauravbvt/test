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
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification
import com.mindalliance.channels.playbook.support.util.CountedSet
import com.mindalliance.channels.playbook.support.drools.RuleBaseSession
import com.mindalliance.channels.playbook.ifm.info.GeoLocation
import com.mindalliance.channels.playbook.ifm.Channels
import com.mindalliance.channels.playbook.ifm.taxonomy.Taxonomy

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
    List<Ref> sharingAgreements = []
    List<Ref> playbooks = []
    List<Ref> taxonomies = []
    List<Ref> analysisElements = []

    @Override
    protected List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['allIssues', 'allInvalidations'])
    }

    static Ref current() {
        PlaybookSession session = (PlaybookSession) Session.get()
        return session.project
    }

    String toString() { name }

    Referenceable doAddToField(String field, Object object) {
        object.project = this.reference
        super.doAddToField(field, object);
    }

    Referenceable doRemoveFromField(String field, Object object) {
        object.project = null
        super.doRemoveFromField(field, object);
    }
    
    // Rulebase queries

    List<Ref> getAllInvalidations() {
        return RuleBaseSession.current().query("invalidsInProject", [this.id], "_invalid").collect{it.reference()}
    }

    List<Ref> getAllIssues() {
        return RuleBaseSession.current().query("issuesInProject", [this.id], "_issue").collect{it.reference()}
    }

    // end rulebase queries

    // Queries

    static List<Ref> findProjectsOfUser(Ref user) {
        return Channels.instance().findProjectsForUser(user)
    }

    Ref findModelNamed(String name) {
        return (Ref) taxonomies.find {it as boolean && it.name == name}
    }

    List<Ref> findAllResources() {
        return findAllResourcesExcept(null)
    }

    Ref findPersonNamed(String name) {
        return (Ref) persons.find {it as boolean && it.name == name}
    }

    Ref findOrganizationNamed(String name) {
        return (Ref) organizations.find {it as boolean && it.name == name}
    }

    List<Ref> findAllResourcesExcept(Ref resource) {
        List<Ref> resources = []
        resources.addAll(persons.findAll {res -> res as boolean && res != resource })
        resources.addAll(organizations.findAll {res -> res as boolean && res != resource })
        resources.addAll(teams.findAll {res -> res as boolean && res != resource })
        organizations.each {org ->
            resources.addAll(org.systems.findAll {res -> res as boolean && res != resource })
            resources.addAll(org.positions.findAll {res -> res as boolean && res != resource })
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
        return (Ref) playbooks.find {pb ->
            pb as boolean && pb.name == name
        }
    }

    Ref findParticipation(Ref user) {
        Ref p = (Ref) participations.find {p -> p as boolean && p.user == user }
        return p
    }


    List<String> findAllPlaceNames() {
        List<String> names = []
        places.each {place -> if (place as boolean) names.add(place.name)}
        return names
    }

    Ref findPlaceNamed(String placeName) {
        Ref namedPlace = (Ref) places.find {place -> place as boolean && place.name == placeName }
        return namedPlace
    }

    boolean atleastOnePlaceTypeDefined() {
        Ref taxonomy = (Ref) taxonomies.find {model ->
            taxonomy as boolean && taxonomy.placeTypes.size() > 0
        }
        return model != null
    }

    List<Ref> findAllTypes(String typeType) {
        List<Ref> types = []
        taxonomies.each {model ->
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
        taxonomies.any {model ->
            List list = model."$propName"
            list.any {type ->
                if (type as boolean && type.name.equalsIgnoreCase(elementTypeName)) {
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
            if (type as boolean && type.narrows(elementType)) types.add(type)
        }
        return types
    }

    List<Ref> findAllTypesNarrowingAny(List<Ref> elementTypes) {
        Set<Ref> types = new HashSet<Ref>()
        elementTypes.each {elementType ->
            if (elementType as boolean) types.addAll(this.findAllTypesNarrowing(elementType))
        }
        return types as List
    }

    List<Ref> findPlaceTypesNarrowing(Ref placeType) {
        List<Ref> narrowing = []
        taxonomies.each {model ->
            if (model as boolean) {
                model.placeTypes.each {pt ->
                    if (placeType == null && pt as boolean && pt.parent == null) { // top level place types narrow undefined place type
                        narrowing.add(pt)
                    }
                    else if (pt as boolean && pt.parent == placeType) {
                        narrowing.add(pt)
                    }
                }
            }
        }
        return narrowing
    }

    List<Ref> findAllAgentsMatchingSpec(AgentSpecification spec, Ref event) {
        return (List<Ref>) findAllAgents().findAll {agent ->
            agent as boolean && event as boolean && spec.matches(agent.deref(), event.deref())
        }
    }

    List<Ref> findAllAgreementsOf(Ref resource) {
        List<Ref> ags = (List<Ref>) sharingAgreements.findAll {agreement ->
            agreement as boolean && agreement.source == resource
        }
        return ags ?: []
    }

    // Find all organizations that are not
    // - the organization
    // -  a sub organization of some organization
    // - a parent organization (transitively) of the organization
    List<Ref> findCandidateSubOrganizationsFor(Ref organization) {
        List<Ref> candidates = (List<Ref>) organizations.findAll {org ->
            org as boolean &&
                    org != organization &&
                    !org.parent as boolean &&
                    !organization.allParents().contains(org)
        }
        return candidates
    }

    List<Ref> findAllPositionsAnywhere() {
        List<Ref> allPositions = []
        organizations.each {org -> if (org as boolean) allPositions.addAll(org.positions) }
        return allPositions
    }

    List<Ref> findAllRelationshipsOf(Ref resource) {
        return (List<Ref>) relationships.findAll {rel ->
            rel as boolean && rel.fromAgent == resource || rel.toAgent == resource
        }
    }

    List<Ref> findAgreementsWhereSource(Ref resource) {
        return (List<Ref>) sharingAgreements.findAll {agr -> agr as boolean && agr.source == resource}
    }

    List<Ref> findAgreementsWhereRecipient(Ref resource) {
        return (List<Ref>) sharingAgreements.findAll {agr -> agr as boolean && agr.recipient == resource}
    }

    List<Ref> findAllJurisdictionables() {
        return (List<Ref>) findAllAgents().findAll {agent -> agent as boolean && agent.hasJurisdiction()}
    }

    List<Ref> findAllPlacesOfTypeImplying(Ref placeType) {
        return (List<Ref>) places.findAll {place -> place as boolean && place.placeType as boolean && place.placeType.implies(placeType)}
    }

    List<Ref> findAllAgentsLocatedInPlacesOfTypeImplying(Ref placeType) {
        return (List<Ref>) findAllAgents().findAll {agent ->
            agent.hasLocation() && agent.location.isAPlace() && agent.location.place.placeType as boolean && agent.location.place.placeType.implies(placeType)
        }
    }

    List<Ref> findAllAgentsWithJurisdictionsInPlacesOfTypeImplying(Ref placeType) {
        return (List<Ref>) findAllAgents().findAll {agent ->
            agent.hasJurisdiction() && agent.jurisdiction.isAPlace() && agent.jurisdiction.place.placeType as boolean && agent.jurisdiction.place.placeType.implies(placeType)
        }
    }

    List<Ref> findAllPlacesInAreasOfTypeImplying(Ref areaType) {
        GeoLocation geoLoc
        return (List<Ref>) places.findAll {place -> (geoLoc = place.findGeoLocation()) && geoLoc.isDefined() && geoLoc.areaType.implies(areaType) }
    }

    List<Ref> findAllAgentsLocatedInAreasOfTypeImplying(Ref areaType) {
        return (List<Ref>) findAllAgents().findAll {agent -> agent.hasLocation() && (geoLoc = agent.location.effectiveGeoLocation) && geoLoc.isDefined() && geoLoc.areaType.implies(areaType)}
    }

    List<Ref> findAllAgentsWithJurisdictionsInAreasOfTypeImplying(Ref areaType) {
        return (List<Ref>) findAllAgents().findAll {agent -> agent.hasJurisdiction() && (geoLoc = agent.jurisdiction.effectiveGeoLocation) && geoLoc.isDefined() && geoLoc.areaType.implies(areaType)}
    }

    List<String> findAllRelationshipNames() {  // TODO write a VocabularyManager to keep vocabulary usage count from everywhere
        CountedSet countedSet = new CountedSet();
        // Permanent relationships
        relationships.each {rel -> if (rel as boolean && rel.name) countedSet.add(rel.name)}
        playbooks.each {playbook ->
            if (playbook as boolean) {
                // transient relationships created by Associations
                playbook.informationActs.each {act ->
                    if (act as boolean && act.type == "Association") {
                        if (act.relationshipName) countedSet.add(act.relationshipName)
                        if (act.reverseRelationshipName) countedSet.add(act.reverseRelationshipName)
                    }
                }
                // agent specs in groups
                playbook.groups.each {group ->
                    if (group as boolean) {
                        List names = group.agentSpec.definitions.relationshipDefinitions.relationshipName
                        names.each {name -> if (name) countedSet.add(name)
                        }
                    }
                }
            }
        }
        // In policies
        policies.each {pol ->
            if (pol as boolean) {
                List names = pol.sourceSpec.definitions.relationshipDefinitions.relationshipName
                names.each {name -> if (name) countedSet.add(name)}
                names = pol.recipientSpec.definitions.relationshipDefinitions.relationshipName
                names.each {name -> if (name) countedSet.add(name)}
            }
        }
        // TODO -- Missing other usages of relationship names?
        return countedSet.toList()
    }


    List<String> findAllPurposes() {
        CountedSet countedSet = new CountedSet();
        policies.each {pol -> if (pol as boolean) countedSet.addAll(pol.purposes)}
        sharingAgreements.each {agr -> if (agr as boolean) countedSet.addAll(agr.constraints.allowedPurposes)}
        playbooks.each {playbook ->
            if (playbook as boolean) {
                playbook.informationActs.each {act ->
                    if (act as boolean && act.type == "SharingCommitment") {
                        countedSet.addAll(act.constraints.allowedPurposes)
                    }
                }
            }
        }
        taxonomies.each {m ->
            m.taskTypes.each {tt -> if (tt as boolean) countedSet.addAll(tt.purposes)}
        }
        return countedSet.toList()
    }

    // End queries

    Boolean isParticipant(Ref user) {
        return findParticipation(user) as boolean;
    }

    Boolean isManager(Ref user) {
        Ref ref = findParticipation(user)
        return ref as boolean && ref.manager;
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