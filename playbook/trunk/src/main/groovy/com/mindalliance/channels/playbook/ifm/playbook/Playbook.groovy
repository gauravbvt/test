package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Described
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.support.RefUtils
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.mem.NoSessionCategory
import org.joda.time.Duration

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 1:26:15 PM
 */
class Playbook extends ProjectElement implements Described {
    private static final long serialVersionUID = -1L;

    String name = ''
    String description = ''
    List<Ref> groups = []
    List<Ref> events = []
    List<Ref> informationActs = []

    @Override
    List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['occurrences', 'latestOccurrence'])
    }

    protected List<String> childProperties() {
        return (List<String>)(super.childProperties() + ['groups', 'events', 'informationActs'])
    }

    String toString() {
        return name ?: "Unnamed"
    }

    void beforeStore(ApplicationMemory memory) {
        super.beforeStore(memory)
        if (!events) {
            use(NoSessionCategory) {
                Event initialEvent = new Event(name: 'Initiating event', description: '(automatically created)')
                this.addElement(initialEvent)
                memory.store(initialEvent)
            }
        }
    }

    Ref persist() {
        super.persist()
        if (!events) {
            Event initialEvent = new Event(name: 'Initiating event', description: '(automatically created)')
            initialEvent.persist()
            this.addElement(initialEvent)
            initialEvent.persist()
        }
        return this.reference
    }


    Map toMap() {
        super.toMap()
    }

    void initFromMap(Map map) {
        super.initFromMap(map)
    }

    void addElement(PlaybookElement element) {
        String field;
        switch (element.type) {
            case "Event": field = "events"; break
            case "Group": field = "groups"; break
            default: field = "informationActs"
        }
        doAddToField(field, element)
    }

    List<Ref> getOccurrences() {
        return (List<Ref>) (events + informationActs)
    }

    Ref getLatestOccurrence() {
        Ref latest = null
        Duration latestStart = Duration.ZERO
        this.occurrences.each {occ ->
           Duration occStart = occ.startTime()
           if (occStart > latestStart)  {
               latest = occ
               latestStart = occStart
           }
        }
        return latest
    }

    Referenceable doAddToField(String field, Object val) {
        String actualField = field
        if (!['events', 'groups'].contains(field)) actualField = 'informationActs'
        super.doAddToField(actualField, val)
    }

    Referenceable doRemoveFromField(String field, Object val) {
        String actualField = field
        if (!['events', 'groups'].contains(field)) actualField = 'informationActs'
        return super.doRemoveFromField(actualField, val)
    }

    // QUERIES

    List<Ref> findAllTypes(String typeType) {
        return this.project.findAllTypes(typeType)
    }

    List<String> findAllOtherTypeNames(Ref elementType) {
        return this.project.findAllOtherTypeNames(elementType)
    }

    List<Ref> findCandidateCauses(Ref event) {
        if (!event.cause.isDefined()) {
            return (List<Ref>) (informationActs + events)
        }
        else {
            List<Ref> candidates = (List<Ref>) informationActs.findAll {act ->
                act as boolean && act != event && !act.isAfter(event)
            }
            candidates.addAll(events.findAll {other ->
                other as boolean && other != event && !other.isAfter(event)
            })
            return candidates
        }
    }

    List<Ref> findPriorInformationActs(Ref event, String type) {
        return (List<Ref>) informationActs.findAll {act ->
            act as boolean && act.type == type && event.isAfter(act)
        }
    }

    List<Ref> findInformationActsOfType(String type) {
        return (List<Ref>) informationActs.findAll {act -> act as boolean && act.type == type}
    }

    List<Ref> findPriorInformationActsOfType(String type, Ref event) {
        return (List<Ref>) informationActs.findAll {act -> act as boolean && act.type == type && !act.isAfter(event)}
    }

    List<Ref> findAllAgents() {
        List<Ref> agents = project.findAllAgents()
        agents.addAll(groups)
        return agents
    }

    List<Ref> findAllAgentsExcept(def holder, String propPath) {
        List<Ref> agents = findAllAgents()
        List<Ref> except = [] + RefUtils.get(holder, propPath)    // works with Objects that are not Lists
        agents.removeAll(except)
        return agents
    }

    Ref findEventNamed(String name) {
        return (Ref) events.find {event -> event as boolean && event.name == name}
    }

/*
    // Playbook shows transient relationship by start of event
    boolean createsRelationshipBefore(Relationship relationship, Ref event) {
        return findPriorInformationActsOfType("Association", event).any {association ->
            association as boolean && association.createsMatchingRelationship(relationship)
        }
    }
*/

/*
    // Whether an agent is the same as or implied by another agent at start of an event
    boolean agentImplied(Ref agent, Ref otherAgent, Ref event) { // TODO -- revise
        if (!agent as boolean || !otherAgent as boolean || !event as boolean) return false
        if (agent == otherAgent) return true
        List<Ref> otherResources = otherAgent.getResourcesAt(event)
        // implied if at least one resource defined by the agent is also defined by the other agent
        boolean implied = agent.getResourcesAt(event).any {res -> res.as boolean && otherResources.contains(res)}
        return implied
    }
*/

    List<Ref> findAllOccurrences() {
        return findAllOccurrencesExcept(null)
    }

    List<Ref> findAllOccurrencesExcept(Ref occurrence) {
        List<Ref> occurrences = []
        occurrences.addAll(events.findAll {event -> event as boolean && event != occurrence})
        occurrences.addAll(informationActs.findAll {act -> act as boolean && act != occurrence})
        return occurrences
    }

    List<Ref> findAllInformationActsForAgent(Ref agent) {
        return (List<Ref>) informationActs.findAll {act ->
            act as boolean && agent as boolean &&
                    ((act.actors.contains(agent)) || (act.isFlowAct() && act.targetAgent == agent))
        }
    }

    // Find all topics that are used or apply to an event based on information acts with information about it
    List<String> findAllTopicsAboutEvent(Ref event) {
        TreeSet<String> topics = new TreeSet<String>()
        informationActs.each {ref ->
            if (ref as boolean) {
                InformationAct act = (InformationAct) ref.deref()
                if (act.hasInformation() && act.information.event == event) { // act has information about the event
                    act.information.eventDetails.each {eoi ->
                        topics.add(eoi.topic)                     // topics used
                    }
                    act.information.eventTypes.each {et ->
                        topics.addAll(et.allTopics())                 // topics from assigned eventTypes
                    }
                }
            }
        }
        return topics as List
    }

    // Find all event types that are used or apply to an event based on information acts with information about it
    List<Ref> findAllEventTypesFor(Ref event) {
        List<Ref> eventTypes = []
        informationActs.each {ref ->
            if (ref as boolean) {
                InformationAct act = (InformationAct) ref.deref()
                if (act.hasInformation() && act.information.event == event) { // act has information about the event
                    eventTypes.addAll(act.information.eventTypes)
                }
            }
        }
        return eventTypes
    }


    List<Ref> findAllPriorOccurrencesOf(Ref occurrence) {
        return (List<Ref>) this.occurrences.findAll {occ ->
            occ as boolean && occurrence.isAfter(occ)
        }
    }

    List<Ref> findAllJurisdictionables() {
        return (List<Ref>) findAllAgents().findAll {agent -> agent.hasJurisdiction()}
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

    List<Ref> findAllAgentsLocatedInAreasOfTypeImplying(Ref areaType) {
        return (List<Ref>) findAllAgents().findAll {agent -> agent.hasLocation() && (geoLoc = agent.location.effectiveGeoLocation) && geoLoc.isDefined() && geoLoc.areaType.implies(areaType)}
    }

    List<Ref> findAllAgentsWithJurisdictionsInAreasOfTypeImplying(Ref areaType) {
        return (List<Ref>) findAllAgents().findAll {agent -> agent.hasJurisdiction() && (geoLoc = agent.jurisdiction.effectiveGeoLocation) && geoLoc.isDefined() && geoLoc.areaType.implies(areaType)}
    }

    // end queries

    /**
     * Return classes a project participant can add.
     */
    static List<Class<?>> contentClasses() {
        (List<Class<?>>) [Assignation.class, Association.class, ConfirmationRequest.class,
                Detection.class, InformationRequest.class, InformationTransfer.class,
                Relocation.class, SharingCommitment.class, SharingRequest.class, Task.class,
                Group.class, Event.class
        ]
    }

    void addContents(List<Ref> results) {
        results.addAll(informationActs)
        results.addAll(groups)
        results.addAll(events)
    }
}