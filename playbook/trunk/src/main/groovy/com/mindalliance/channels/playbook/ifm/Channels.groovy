package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.analysis.AnalysisElement
import com.mindalliance.channels.playbook.ifm.playbook.*
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.project.environment.Place
import com.mindalliance.channels.playbook.ifm.project.environment.Policy
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.ifm.project.environment.SharingAgreement
import com.mindalliance.channels.playbook.ifm.project.resources.Organization
import com.mindalliance.channels.playbook.ifm.project.resources.Person
import com.mindalliance.channels.playbook.ifm.taxonomy.*
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.PlaybookApplication
import com.mindalliance.channels.playbook.support.RefUtils

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2008
 * Time: 2:08:19 PM
 */
class Channels extends IfmElement {

    String about
    List<Ref> projects = []
    List<Ref> users = []
    List<Ref> taxonomies = []

    @Override
    protected List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['allItems'])
    }

    static Channels instance() {
        return (Channels) PlaybookApplication.current().getChannels().deref()
    }

    static Ref reference() {   // CAUTION: this will *not* force an initial load of Channels root data
        PlaybookApplication.current().getRoot()
    }

    // Queries

    List<Ref> findAllImpliedTypes(String typeType) {
        List<Ref> types = []
        switch (typeType) {
            case 'EventType': types.add(Event.implicitEventType()); break
        }
        return types
    }

    Ref findProjectNamed(String name) {
        Ref ref = (Ref) projects.find {project -> project as boolean && project.name == name}
        return ref
    }

    Ref findTaxonomyNamed(String name) {
        Ref ref = (Ref) taxonomies.find {taxonomy -> taxonomy as boolean && taxonomy.name == name}
        return ref
    }

    Ref findUser(String uid) {
        return (Ref) users.find {user -> user as boolean && user.userId == uid}
    }

    List<Ref> findProjectsForUser(Ref user) {
        List<Ref> result = []
        projects.each {project ->
            if (project as boolean && project.isParticipant(user)) result.add(project)
        }
        return result
    }

    List<Ref> findUsersNotInProject(Ref project) {
        List<Ref> results
        results = (List<Ref>) users.findAll {user ->
            user as boolean &&
                    project.participations.every {part -> part as boolean && part.user != user}
        }
        return results
    }

    // Taxonomies modifiable by user
    List<Ref> findTaxonomiesForUser(Ref user) {
        List<Ref> result = []
        if (user.analyst)
            taxonomies.each {taxonomy ->
                if (taxonomy as boolean && taxonomy.isAnalyst(user)) result.add(taxonomy)
            }
        return result
    }

    List<Ref> findTaxonomiesVisibleToUser(Ref user) {
        Set<Ref> results = new HashSet<Ref>()
        results.addAll(findTaxonomiesForUser(user)) // all taxonomies writable by user
        findProjectsForUser(user).each {project ->
            results.addAll(project.taxonomies)
        }
        return results as List<Ref>
    }

    List<Ref> findAllTypes(String typeType) {
        List<Ref> types = []
        types.addAll(findAllImpliedTypes(typeType))
        taxonomies.each {taxonomy ->
            if (taxonomy as boolean) types.addAll(taxonomy.findAllTypes(typeType))
        }
        return types
    }

    List<String> findAllOtherTypeNames(Ref elementType) {
        List<Ref> allTypes = findAllTypes(elementType.type)
        List<String> otherNames = []
        allTypes.each {type ->
            if (type as boolean && type != elementType) otherNames.add(type.name)
        }
        return otherNames
    }

    List<String> findAllPurposes() {
        return [] // TODO - return empty, else inter-project privacy breach
    }

    List<Ref> findAllProjectsOfUser(Ref user) {
        return (List<Ref>) projects.findAll {project -> project.participations.any {part -> part.user == user}}
    }

    Ref findOrganizationOfResource(Ref res) {
        Ref organization = null
        RefUtils.getUserProjects().any {project ->
            project.organizations.any { org ->
                if (org.positions.contains(res)) {
                    organization = org
                }
                else if (org.systems.contains(res)) {
                    organization = org
                }
            }
        }
        return organization
    }

    Ref findOrganizationOfPolicy(Ref policy) {
        Ref organization = null
        RefUtils.getUserProjects().any {project ->
            project.organizations.any {org ->
                if (org.policies.contains(policy)) {
                    organization = org
                }
            }
        }
        return organization
    }


    Ref findPlaybookOfElement(Ref ref) {
        Ref playbook = null
        PlaybookElement element = (PlaybookElement) ref.deref()
        if (!element) return null
        RefUtils.getUserProjects().any {project ->
            project.playbooks.any {pb ->
                switch (element) {
                    case Group.class:
                        if (pb.groups.contains(ref)) playbook = pb; break
                    case InformationAct.class:
                        if (pb.informationActs.contains(ref)) playbook = pb; break
                    case Event.class:
                        if (pb.events.contains(ref)) playbook = pb; break
                }
            }
        }
        return playbook
    }


    Ref findProjectOfElement(Ref ref) {
        Ref project = null
        InProject element = (InProject) ref.deref()
        if (element == null) return null
        RefUtils.getUserProjects().any {proj ->
            switch (element) {
                case Participation.class:
                    if (proj.participations.contains(ref)) project = proj; break
                case Person.class:
                    if (proj.persons.contains(ref)) project = proj; break
                case Organization.class:
                    if (proj.organizations.contains(ref)) project = proj; break
                case Place.class:
                    if (proj.places.contains(ref)) project = proj; break
                case Relationship.class:
                    if (proj.relationships.contains(ref)) project = proj; break
                case Policy.class:
                    if (proj.policies.contains(ref)) project = proj; break
                case SharingAgreement.class:
                    if (proj.sharingAgreements.contains(ref)) project = proj; break
                case Playbook.class:
                    if (proj.playbooks.contains(ref)) project = proj; break
                case Taxonomy.class:
                    if (proj.taxonomies.contains(ref)) project = proj; break
                case AnalysisElement.class:
                    if (proj.analysisElements.contains(ref)) project = proj; break
            }
        }
        return project
    }

    Ref findTaxonomyOfElement(Ref ref) {
        Ref taxonomy = null
        TaxonomyElement element = (TaxonomyElement) ref.deref()
        if (element == null) return null
        findTaxonomiesVisibleToUser(RefUtils.getUser()).any {taxo ->
            switch (element) {
                case TaxonomyParticipation.class:
                    if (taxo.participations.contains(ref)) taxonomy = taxo; break
                case AreaType.class:
                    if (taxo.areaTypes.contains(ref)) taxonomy = taxo; break
                case EventType.class:
                    if (taxo.eventTypes.contains(ref)) taxonomy = taxo; break
                case MediumType.class:
                    if (taxo.mediumTypes.contains(ref)) taxonomy = taxo; break
                case OrganizationType.class:
                    if (taxo.organizationTypes.contains(ref)) taxonomy = taxo; break
                case PlaceType.class:
                    if (taxo.placeTypes.contains(ref)) taxonomy = taxo; break
                case Role.class:
                    if (taxo.roles.contains(ref)) taxonomy = taxo; break
                case TaskType.class:
                    if (taxo.taskTypes.contains(ref)) taxonomy = taxo;
            }
        }
        return taxonomy
    }

    // end queries

    static List adminClasses() {
        [User.class]
    }

    static List contentClasses() {
        [User.class, Project.class, Taxonomy.class]
    }

    static boolean isSet(Ref ref) {  // always called in application scope
        return ref != null && (ref.isComputed() || ref.isAttached() || PlaybookApplication.current().isStored(ref))
    }
    
}