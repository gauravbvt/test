package com.mindalliance.channels.playbook.ifm.model

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:09:50 PM
*/
class Model  extends IfmElement {

    String name
    Boolean shared

    List<Ref> participations = []
    List<Ref> areaTypes = []
    List<Ref> domains = []
    List<Ref> eventTypes = []
    List<Ref> issueTypes = []
    List<Ref> organizationTypes = []
    List<Ref> placeTypes = []
    List<Ref> purposeTypes = []
    List<Ref> relationshipTypes = []
    List<Ref> roles = []
    List<Ref> taskTypes = []

    String toString() {
        name
    }

    protected List<String> transientProperties() {
        return super.transientProperties() + ['elements']
    }


    List<Ref> getElements() {
        List<Ref> elements = []
        elements.addAll(domains)
        elements.addAll(eventTypes)
        elements.addAll(issueTypes)
        elements.addAll(placeTypes)
        elements.addAll(purposeTypes)
        elements.addAll(relationshipTypes)
        elements.addAll(roles)
        elements.addAll(taskTypes)
        return elements
    }

    Boolean isAnalyst( Ref user ) {
        return participations.find { it.user.id == user.id } != null
    }

    /**
     * Return what model content an analyst can create.
     */
    static List<Class<?>> contentClasses() {
        [
          Domain.class, EventType.class, IssueType.class,
          PlaceType.class, AreaType.class, OrganizationType.class,
          PurposeType.class, RelationshipType.class,
          Role.class, TaskType.class
        ]
    }

    void addContents( List<Ref> results ) {
        results.addAll( elements );
    }

    /**
     * Return what system objects an analyst can create.
     */
    static List<Class<?>> analystClasses() {
        [ Model.class ]
    }
}