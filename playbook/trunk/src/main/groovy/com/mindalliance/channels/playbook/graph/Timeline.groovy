package com.mindalliance.channels.playbook.graph

import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.playbook.*
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.models.Container
import com.mindalliance.channels.playbook.ref.Referenceable
import org.apache.log4j.Logger

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 4, 2008
 * Time: 9:20:15 AM
 */
class Timeline extends PlaybookGraph {

    TreeMap<Duration, Set<Event>> timed = new TreeMap<Duration, Set<Event>>()

    Timeline(Container container) {
        super(container)
    }

    Map getStyleTemplate() {
        return super.getStyleTemplate() + [
           time: [shape: 'plaintext', fontsize:'9'],
           time_edge: [dir: 'none']
        ]
    }

    void buildContent(GraphVizBuilder builder) {
        processData()
         buildTimePoints(builder)
        buildOccurrences(builder)
        buildTimings(builder)
        buildCausality(builder)
    }

    void processData() {
        container.iterator().each {ref ->
            Referenceable el = ref.deref()
            switch(el) {
                case Event.class: processEvent((Event)el); break
                case Playbook.class: processPlaybook((Playbook)el); break
                //default: Logger.getLogger(this.class).warn("Can't display $el")
            }
        }
    }

    void processEvent(Event event) {
        Duration start = event.startTime()
        if (timed[start] == null) timed[start] = new HashSet<Event>()
        timed[start].add(event)
    }

    void processPlaybook(Playbook pb) {   // TODO - not needed
        pb.events.each {ref ->
            if(ref as boolean) processEvent((Event)ref.deref())
        }
        pb.informationActs.each {ref ->
            if(ref as boolean) processEvent((Event)ref.deref())
        }
    }

    void buildTimePoints(GraphVizBuilder builder) {
        String priorName = null
        timed.keySet().each {dur ->
            String durText = durationToText(dur)
            builder.node(name: durText, label:durText, template:'time')
            if (priorName) builder.edge(source:priorName, target: durText, template:'time_edge')
            priorName = durText
        }
    }

    void buildOccurrences(GraphVizBuilder builder) {
        timed.each {dur, occSet ->
            occSet.each {occ ->
                if (occ instanceof InformationAct) {
                    Agent agent = (Agent)occ.actorAgent.deref()
                    builder.cluster(name:nameFor(occ) + nameFor(agent), label:labelFor(agent), URL:urlFor(agent), template:'agent') {
                        builder.node(name:nameFor(occ), label:labelFor(occ), URL:urlFor(occ), template:templateFor(occ))
                    }
                }
                else {
                    builder.node(name:nameFor(occ), label:labelFor(occ), URL:urlFor(occ), template:templateFor(occ))
                }

            }
        }
    }

    void buildTimings(GraphVizBuilder builder) {
        timed.each {dur, occSet ->
            builder.subgraph(rank:'same') {
                builder.node(name: durationToText(dur))
                occSet.each {occ ->
                    builder.node(name:nameFor(occ))
                }
            }
        }
    }

    void buildCausality(GraphVizBuilder builder) {
        timed.each {dur, occSet ->
            occSet.each {occ ->
                Ref eventRef = occ.cause.trigger
                if (eventRef) {
                    Event cause = (Event)eventRef.deref()
                    builder.edge(source:nameFor(cause), target:nameFor(occ))
                }
            }
        }
    }

}