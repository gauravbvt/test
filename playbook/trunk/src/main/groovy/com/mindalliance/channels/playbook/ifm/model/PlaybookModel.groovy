package com.mindalliance.channels.playbook.ifm.model

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ifm.Describable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.support.RefUtils

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 1:09:50 PM
 */
class PlaybookModel extends IfmElement implements Describable {

    String name
    String description
    Boolean shared

    List<Ref> participations = []
    List<Ref> areaTypes = []
    List<Ref> domains = []
    List<Ref> eventTypes = []
    List<Ref> issueTypes = []
    List<Ref> mediumTypes = []
    List<Ref> organizationTypes = []
    List<Ref> placeTypes = []
    List<Ref> roles = []
    List<Ref> taskTypes = []

    String toString() {
        name
    }

    protected List<String> transientProperties() {
        return super.transientProperties() + ['elements', 'participatingUsers']
    }


    Referenceable doAddToField(String field, Object object) {
        object.model = this.reference
        super.doAddToField(field, object)
    }

    Referenceable doRemoveFromField(String field, Object object) {
        object.model = null
        super.doRemoveFromField(field, object)
    }


    List<Ref> getElements() {
        List<Ref> elements = []
        elements.addAll(domains)
        elements.addAll(eventTypes)
        elements.addAll(issueTypes)
        elements.addAll(placeTypes)
        elements.addAll(roles)
        elements.addAll(taskTypes)
        return elements
    }

    List<Ref> getParticipatingUsers() {
        return participations.collect {participation -> participation.user}
    }

    // Queries

    List<Ref> findAllTypes(String typeType) {
        String propName = RefUtils.decapitalize("${typeType}s")
        return this."$propName"
    }

    List<String> findAllOtherTypeNames(Ref elementType) {
        List<Ref> allTypes = findAllTypes(elementType.type)
        List<String> otherNames = []
        allTypes.each {type ->
            if (type != elementType) otherNames.add(type.name)
        }
        return otherNames
    }

    Ref findType(String typeType, String name) {
        String typeName = RefUtils.decapitalize(typeType)
        Ref namedType = this."${typeName}s".find {type -> type.name == name}
        return namedType
    }

    List<String> findInheritedTopics(Ref eventType) {
        List<String> inheritedTopics = []
        eventType.narrowedTypes.each {nt ->
            inheritedTopics.addAll(nt.topics)
            inheritedTopics.addAll(findInheritedTopics(nt))
        }
        return inheritedTopics.sort()
    }

    // End queries

    Boolean isAnalyst(Ref user) {
        return participations.find { it.user.id == user.id } != null
    }

    /**
     * Return what model content an analyst can create.
     */
    static List<Class<?>> contentClasses() {
        [
                Domain.class, EventType.class, IssueType.class,
                PlaceType.class, AreaType.class, OrganizationType.class,
                MediumType.class,
                Role.class, TaskType.class
        ]
    }

    void addContents(List<Ref> results) {
        results.addAll(elements);
    }

    /**
     * Return what system objects an analyst can create.
     */
    static List<Class<?>> analystClasses() {
        [PlaybookModel.class]
    }
}