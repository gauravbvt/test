package com.mindalliance.channels.playbook.ifm.taxonomy

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ifm.Described
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.support.RefUtils
import com.mindalliance.channels.playbook.support.util.CountedSet
import com.mindalliance.channels.playbook.ifm.Channels
import com.mindalliance.channels.playbook.ifm.playbook.Event
import com.mindalliance.channels.playbook.query.Query

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 1:09:50 PM
 */
class Taxonomy extends IfmElement implements Described {

    String name
    String description
    Boolean shared

    List<Ref> participations = []
    List<Ref> areaTypes = []
    List<Ref> eventTypes = []
    List<Ref> mediumTypes = []
    List<Ref> organizationTypes = []
    List<Ref> placeTypes = []
    List<Ref> roles = []
    List<Ref> taskTypes = []

    String toString() {
        name
    }

    protected List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['elements', 'participatingUsers', 'allEventTypes'])
    }

    Set keyProperties() {
        return (super.keyProperties() + ['name', 'description']) as Set
    }

    Referenceable doAddToField(String field, Object object) {
        object.taxonomy = this.reference
        super.doAddToField(field, object)
    }

    Referenceable doRemoveFromField(String field, Object object) {
        object.taxonomy = null
        super.doRemoveFromField(field, object)
    }

/*    Map toMap() {
        super.toMap()
    }

    void setEventTypes(List<Ref> list) {
        super.setEventTypes(list)
    }

    void addEventType(Ref et) {
        eventTypes.add(et)
    }*/


    List<Ref> getAllEventTypes() {
        List<Ref> all = []
        all.addAll(eventTypes)
        all.addAll(Event.findAllImplicitEventTypes())
        return all
    }

    List<Ref> getParticipatingUsers() {
        return participations.collect {participation -> participation.user}
    }

    // Queries

    static List<Ref> findTaxonomiesOfUser(Ref user) {
        return Channels.instance().findTaxonomiesForUser(user)
    }

    static List<Ref> findAllImplicitTypes(String typeType) {
        switch(typeType) {
            case 'EventType': return (List<Ref>)Query.execute(Event.class, 'findAllImplicitEventTypes')
            default: return []
        }
    }


    List<Ref> findAllTypes(String typeType) {
        String propName = RefUtils.decapitalize("${typeType}s")
        List<Ref> allTypes = this."$propName"
        allTypes.addAll(Taxonomy.findAllImplicitTypes(typeType))
        return allTypes
    }

    List<String> findAllOtherTypeNames(Ref elementType) {
        List<Ref> allTypes = findAllTypes(elementType.type)
        List<String> otherNames = []
        allTypes.each {type ->
            if (type as boolean && type != elementType) otherNames.add(type.name)
        }
        return otherNames
    }

    Ref findType(String typeType, String name) {
        Ref namedType = (Ref)findAllTypes(typeType).find {type -> type as boolean && type.name == name}
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

    List<String> findAllPurposes() {
        CountedSet countedSet = new CountedSet();
        m.taskTypes.each {tt -> if (tt as boolean) countedSet.addAll(tt.purposes)}
        return countedSet.toList()
    }

    // End queries

    Boolean isAnalyst(Ref user) {
        return participations.find { it as boolean && it.user.id == user.id } != null
    }

    List<Ref> getElements() {
        List<Ref> elements = []
        elements.addAll(areaTypes)
        elements.addAll(eventTypes)
        elements.addAll(mediumTypes)
        elements.addAll(organizationTypes)
        elements.addAll(placeTypes)
        elements.addAll(roles)
        elements.addAll(taskTypes)
        return elements
    }

    /**
     * Return what model content an analyst can create.
     */
    static List<Class<?>> contentClasses() {
        [
                AreaType.class, EventType.class,
                MediumType.class,
                PlaceType.class, OrganizationType.class,
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
        [Taxonomy.class]
    }
}