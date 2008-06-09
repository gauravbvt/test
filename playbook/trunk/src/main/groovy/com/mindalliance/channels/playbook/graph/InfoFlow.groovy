package com.mindalliance.channels.playbook.graph

import com.mindalliance.channels.playbook.support.models.Container
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.playbook.Event
import org.apache.log4j.Logger
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.project.resources.Resource
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.ifm.playbook.Playbook

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 7, 2008
 * Time: 2:47:44 PM
 */
class InfoFlow extends PlaybookGraph {

    Set<InformationAct> acts = new HashSet()
    Set<Agent> agents = new HashSet()
    Set<Event> events = new HashSet() // events that are not information acts
    Map<String, List<String>> links = new HashMap<String, List<String>>()

    InfoFlow(Container container) {
        super(container)
    }

    Map getStyleTemplate() {
        return super.getStyleTemplate() + [
                about_edge: [dir: 'none', style: 'dashed']
        ]
    }

    void buildContent(GraphVizBuilder builder) {
        processData()
        buildAgents()
        buildLinks()
    }

    void processData() {
        container.iterator().each {ref ->
            Referenceable el = ref.deref()
            switch (el) {
                case Agent.class: processAgent(el); break
                case InformationAct.class: processAct(el); break
                case Event.class: processEvent(el); break
                default: Logger.getLogger(this.class).warn("Can't display $el in info flow")
            }
        }
    }

    void processAgent(Agent agent) {
        switch (agent) {
            case Resource.class: processResource(agent); break
            default: processPlaybookAgent(agent); break
        }
    }

    // Add all acts this resource is actor or target of in all playbooks in resource's project
    void processResource(Resource res) {
        acts.addAll(Query.execute(res, "findAllInformationActsForResource"))
        agents.add(res)
    }

    // Add all acts agent is actor or target of in agent's playbook
    void processPlaybookAgent(Agent agent) {
        Playbook playbook = agent.playbook
        assert playbook
        acts.addAll(Query.execute(playbook, "findAllInformationActsForAgent", agent.reference))
    }

    // Add act, actor and target agents, and all events it triggers
    void processAct(InformationAct act) {
        acts.add(act)
        // TODO - Add all events it triggers
    }

    // Process all acts that involve info about the event
    void processEvent(Event event) {
        // TODO
    }

    // Add agent nodes, containing its acts and info/needs/responsibilities/agreements from any acts where actor or target (based on akind of ct). Setup links.
    void buildAgents(GraphVizBuilder builder) {

    }

    void buildLinks(GraphVizBuilder builder) {

    }

}