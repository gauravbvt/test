package com.mindalliance.channels.playbook.graph

import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.playbook.*
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.models.Container

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 4, 2008
 * Time: 9:20:15 AM
 */
class Timeline extends PlaybookGraph {

    TreeMap<Duration, List<Event>> timed = new TreeMap<Duration, List<Event>>()
    
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
        container.iterator().each {occ ->
            Duration start = occ.startTime()
            if (timed[start] == null) timed[start] = []
            timed[start].add((Event)occ.deref())
        }
        buildTimePoints(builder)
        buildOccurrences(builder)
        buildTimings(builder)
        buildCausality(builder)
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
        timed.each {dur, occList ->
            occList.each {occ ->
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
        timed.each {dur, occList ->
            builder.subgraph(rank:'same') {
                builder.node(name: durationToText(dur))
                occList.each {occ ->
                    builder.node(name:nameFor(occ))
                }
            }
        }
    }

    void buildCausality(GraphVizBuilder builder) {
        timed.each {dur, occList ->
            occList.each {occ ->
                Ref eventRef = occ.cause.trigger
                if (eventRef) {
                    Event cause = (Event)eventRef.deref()
                    builder.edge(source:nameFor(cause), target:nameFor(occ))
                }
            }
        }
    }

}