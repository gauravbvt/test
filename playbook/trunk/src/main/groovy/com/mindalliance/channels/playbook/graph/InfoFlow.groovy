package com.mindalliance.channels.playbook.graph

import com.mindalliance.channels.playbook.support.models.Container
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.playbook.Event
import org.apache.log4j.Logger
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.project.resources.Resource
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ifm.info.InformationNeed
import com.mindalliance.channels.playbook.ifm.playbook.Task
import com.mindalliance.channels.playbook.ifm.playbook.InformationRequest

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 7, 2008
 * Time: 2:47:44 PM
 */
class InfoFlow extends PlaybookGraph {

    Set<Ref> acts = new HashSet()
    Set<Ref> agents = new HashSet()
    Set<Ref> events = new HashSet() // events that are not information acts
    List links = []

    InfoFlow(Container container) {
        super(container)
    }

    Map getStyleTemplate() {
        return super.getStyleTemplate() + [
                aboutEdge: [dir: 'none', style: 'dashed'],
                flowEdge: [fontsize:'8']
        ]
    }

    void buildContent(GraphVizBuilder builder) {
        processData()
        buildAgents(builder)
        buildEvents(builder)
        buildLinks(builder)
    }

    void processData() {
        container.iterator().each {ref ->
            Referenceable el = ref.deref()
            switch (el) {
                case Agent.class: processAgent((Agent) el); break
                case InformationAct.class: processAct((InformationAct) el); break
                case Event.class: processEvent((Event) el); break
                case Playbook.class: processPlaybook((Playbook)el); break
                default: Logger.getLogger(this.class).warn("Can't display $el in info flow")
            }
        }
    }

    void processPlaybook(Playbook pb) {
        pb.events.each {ref -> processEvent((Event)ref.deref())}
        pb.informationActs.each {ref -> processAct((InformationAct)ref.deref())}
        pb.teams.each {ref -> processAgent((Agent)ref.deref())}
        pb.groups.each {ref -> processAgent((Agent)ref.deref())}
    }

    void processAgent(Agent agent) {
        switch (agent) {
            case Resource.class: processResource((Resource) agent); break
            default: processPlaybookAgent(agent); break
        }
    }

    // Add all acts this resource is actor or target of in all playbooks in resource's project
    void processResource(Resource res) {
        acts.addAll(Query.execute(res, "findAllInformationActsForResource"))
        agents.add(res.reference)
    }

    // Add all acts agent is actor or target of in agent's playbook
    void processPlaybookAgent(Agent agent) {
        Playbook playbook = agent.playbook
        assert playbook
        acts.addAll(Query.execute(playbook, "findAllInformationActsForAgent", agent.reference))
    }

    // Add act, actor and target agents
    void processAct(InformationAct act) {
        acts.add(act.reference)
        agents.add(act.actorAgent)
        if (act.isFlowAct()) agents.add(act.targetAgent)
    }

    // Add all acts with information/need about the event
    void processEvent(Event event) {
        events.add(event.reference)
        acts.addAll(Query.execute(event, "findAllInformationActsAboutEvent"))
    }

    // Add agent nodes, containing its acts and info/needs/responsibilities/agreements from any acts where actor or target (based on akind of ct). Setup links.
    void buildAgents(GraphVizBuilder builder) {
        agents.each {agentRef ->
            Agent agent = agentRef.deref()
            builder.cluster(name: nameFor(agent), label: labelFor(agent), URL: urlFor(agent), template: 'agent') {
                acts.each {actRef ->
                    InformationAct act = actRef.deref()
                    // Actor's acts
                    if (act.actorAgent == agentRef) {
                        builder.node(name: nameFor(act), label: labelFor(act), URL: urlFor(act), template: templateFor(act))
                    }
                    // Actor's acquired information
                    if (act.hasInformation() && ((act.isFlowAct() && act.targetAgent == agentRef) ||
                                                 (!act.isFlowAct() && act.actorAgent == agentRef))) {
                        Information info = act.information
                        String name = "${new Random().nextLong()}"
                        builder.node(name: name, label: labelFor(info), URL: urlFor(act), template: 'info')
                        links.add([nameFor(act), name, durationToText(act.startTime()), 'flowEdge'])
                        Event subject = (Event)info.event.deref()
                        events.add(info.event)
                        links.add([name, nameFor(subject), '', 'aboutEdge'])
                    }
                    // Actor's task-acquired information needs
                    if (act instanceof Task && act.actorAgent == agentRef) {
                        act.informationNeeds.each {need ->
                            String name = "${new Random().nextLong()}"
                            builder.node(name: name, label: labelFor(need), URL: urlFor(act), template: 'need')
                            links.add([nameFor(act), name, '', 'aboutEdge'])
                        }
                    }
                    if (act instanceof InformationRequest && act.targetAgent == agentRef) {
                        InformationNeed need = act.informationNeed
                        String name = "${new Random().nextLong()}"
                        builder.node(name: name, label: labelFor(need), URL: urlFor(act), template: 'need')
                        links.add([nameFor(act), name, durationToText(act.startTime()), 'flowEdge'])
                    }
                    // TODO - add dynamic relationships, assignments, agreements
                }
            }
        }
    }

    void buildEvents(GraphVizBuilder builder) {
        events.each {eventRef ->
            Event event = (Event)eventRef.deref()
            builder.node(name:nameFor(event), label:labelFor(event), URL: urlFor(event), template:templateFor(event))
        }
    }

    void buildLinks(GraphVizBuilder builder) {
       links.each {link ->
           builder.edge(source:link[0], target:link[1], label:link[2], template:link[3])
        }
    }


}