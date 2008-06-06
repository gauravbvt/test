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

    void buildContent() {
        container.iterator().each {occ ->
            Duration start = occ.startTime()
            if (timed[start] == null) timed[start] = []
            timed[start].add((Event)occ.deref())
        }
        buildTimePoints()
        buildOccurrences()
        buildTimings()
        buildCausality()
    }

    void buildTimePoints() {
        String priorName = null
        timed.keySet().each {dur ->
            String durText = durationToText(dur)
            this.builder.node(name: durText, label:durText, template:'time')
            if (priorName) this.builder.edge(source:priorName, target: durText, template:'time_edge')
            priorName = durText
        }
    }

    void buildOccurrences() {
        timed.each {dur, occList ->
            occList.each {occ ->
                if (occ instanceof InformationAct) {
                    Agent agent = (Agent)occ.actorAgent.deref()
                    this.builder.cluster(name:nameFor(occ) + nameFor(agent), label:labelFor(agent), URL:urlFor(agent), template:'agent') {
                        this.builder.node(name:nameFor(occ), label:labelFor(occ), URL:urlFor(occ), template:templateFor(occ))
                    }
                }
                else {
                    this.builder.node(name:nameFor(occ), label:labelFor(occ), URL:urlFor(occ), template:templateFor(occ))
                }

            }
        }
    }

    void buildTimings() {
        timed.each {dur, occList ->
            this.builder.subgraph(rank:'same') {
                this.builder.node(name: durationToText(dur))
                occList.each {occ ->
                    this.builder.node(name:nameFor(occ))
                }
            }
        }
    }

    void buildCausality() {
        timed.each {dur, occList ->
            occList.each {occ ->
                Ref eventRef = occ.cause.trigger
                if (eventRef) {
                    Event cause = (Event)eventRef.deref()
                    this.builder.edge(source:nameFor(cause), target:nameFor(occ))
                }
            }
        }
    }

}