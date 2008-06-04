package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Described
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.support.RefUtils
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import org.joda.time.Duration
import com.mindalliance.channels.playbook.graph.Timeline
import com.mindalliance.channels.playbook.ifm.Described

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:26:15 PM
*/
class Playbook extends ProjectElement implements Described {

    String name = ''
    String description = ''
    List<Ref> teams = []
    List<Ref> groups = []
    List<Ref> events = []
    List<Ref> informationActs = []

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
             case "Team": field = "teams"; break
             default: field = "informationActs"
         }
         doAddToField(field, element)
     }


    Referenceable doAddToField( String field, Object val ) {
        val.playbook = this.reference
        String actualField = field
        if (!['events', 'groups', 'teams'].contains(field)) actualField = 'informationActs'
        super.doAddToField(actualField, val)
    }

    Referenceable doRemoveFromField(String field, Object val) {
        val.playbook = null
        String actualField = field
        if (!['events', 'groups', 'teams'].contains(field)) actualField = 'informationActs'
        return super.doRemoveFromField(actualField, val)
    }

    // DIAGRAMS

    // Generate causal diagram as SVG
    // urls must be "javascript:svg_wicket_call('__CALLBACK__','some ref id')"
    String causalDiagram(String[] dimensions) { // TODO
        Timeline timeline = new Timeline(this, dimensions)
        String svg = timeline.svg
        // String svg = "<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='600px' height='400px' style='display:block;'><g id='node2' class='node'><title>T1_activity1</title><a xlink:href='javascript:svg_wicket_call(&#39;__CALLBACK__&#39;,&#39;some_ref_id&#39;)' xlink:title='Activity\\n(Steal explosives)'><ellipse style='fill:#e0eeee;stroke:black;' cx='2' cy='21' rx='71.8428' ry='29.4156'/><text text-anchor='middle' x='2' y='24' style='font-family:Nimbus Sans L;font-weight:regular;font-size:11.34pt;'>Activity</text><text text-anchor='middle' x='2' y='7' style='font-family:Nimbus Sans L;font-weight:regular;font-size:11.34pt;'>(Steal explosives)</text></a></g></svg>";
        return svg
    }

    // end diagrams

    // QUERIES

    List<Ref> findAllTypes(String typeType) {
        return this.project.findAllTypes(typeType)
    }

    List<String> findAllOtherTypeNames(Ref elementType) {
         return this.project.findAllOtherTypeNames(elementType)
    }

    List<Ref> findCandidateCauses(Ref event) {
         List<Ref> candidates = informationActs.findAll { act ->
              !act.isAfter(event)
         }
        candidates.addAll(events.findAll {other ->
            !other.isAfter(event)
        })
        return candidates
    }

    List<Ref> findPriorInformationActs(Ref event, String type) {
        return informationActs.findAll {act ->
            act.type == type && event.isAfter(act)
        }
    }

    List<Ref> findInformationActsOfType(String type) {
        return informationActs.findAll {act -> act.type == type}
    }

    List<Ref> findPriorInformationActsOfType(String type, Ref event) {
        return informationActs.findAll {act -> act.type == type && !act.isAfter(event)}
    }

    List<Ref> findAllAgents() {
        List<Ref> agents = getProject().findAllResources()
        agents.addAll(teams)
        agents.addAll(groups)
        return agents
    }

    List<Ref> findAllAgentsExcept(def holder, String propPath) {
        List<Ref> agents = findAllAgents()
        Ref agent = RefUtils.get(holder, propPath)
        agents.remove(agent)
        return agents
    }

    // Playbook shows transient relationship by start of event
    boolean createsRelationshipBefore(Relationship relationship, Event event) {
       return findAllPriorInformationActsOfType("Association", event).any {association ->
           association.createsMatchingRelationship(relationship)
       }
    }

    // Whether an agent is the same as or implied by another agent at start of an event
    boolean agentImplied(Ref agent, Ref otherAgent, Event event) {
        if (agent == null || otherAgent == null) return false
        if (agent == otherAgent) return true
        List<Ref> otherResources = otherAgent.getResourcesAt(event)
        // implied if it is not true that at least one resource defined by the agent is not also defined by the other agent
        boolean implied = !agent.getResourcesAt(event).any {res -> !otherResources.contains(res)}
        return implied
    }

    List<Ref> findAllOccurrences() {
        return findAllOccurrencesExcept(null)
    }

    List<Ref> findAllOccurrencesExcept(Ref occurrence) {
        List<Ref> occurrences = []
        occurrences.addAll(events.findAll{it != occurrence})
        occurrences.addAll(informationActs.findAll{it != occurrence})
        return occurrences
    }

    // end queries

    /**
     * Return classes a project participant can add.
     */
    static List<Class<?>> contentClasses() {
        [ Assignation.class, Association.class, Confirmation.class, Denial.class,
          InformationTransfer.class, Detection.class, InformationRequest.class,
          SharingRequest.class, SharingCommitment.class,
          Task.class, Verification.class, Group.class, Team.class, Event.class
        ]
    }

    void addContents( List<Ref> results ) {
        results.addAll( informationActs )
        results.addAll (groups)
        results.addAll (teams)
        results.addAll (events)
    }
}