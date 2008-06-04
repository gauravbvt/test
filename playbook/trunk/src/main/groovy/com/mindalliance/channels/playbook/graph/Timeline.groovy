package com.mindalliance.channels.playbook.graph

import org.joda.time.Duration
import org.joda.time.Period
import com.mindalliance.channels.playbook.ifm.playbook.*
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Named

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 4, 2008
 * Time: 9:20:15 AM
 */
class Timeline extends DirectedGraph {

    TreeMap<Duration, List<Event>> timed = new TreeMap<Duration, List<Event>>()
    
    Timeline(Playbook playbook, String[] dimensions) {
        super(playbook, dimensions)
    }

    Timeline(Playbook playbook, int width, int height) {
        super(playbook, width, height)
    }

    Map getStyleTemplate() {
        return super.getStyleTemplate() + [
           time: [shape: 'plaintext', fontsize:'16'],
           time_edge: [dir: 'none']
        ]
    }

    void buildContent() {
        playbook.findAllOccurrences().each {occ ->
            Duration start = occ.startTime()
            if (timed[start] == null) timed[start] = []
            timed[start].add((Event)occ.deref())
        }
        buildTimePoints()
        buildTimedOccurrences()
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

    void buildTimedOccurrences() {
        timed.each {dur, occList ->
            this.builder.subGraph(rank:'same') {
                this.builder.node(name: durationToText(dur))
                occList.each {occ ->
                    if (occ instanceof InformationAct) {
                        Agent agent = (Agent)occ.actorAgent.deref()
                        this.builder.cluster(name:nameFor(agent), label:labelFor(agent), template:'agent') {
                            this.builder.node(name:nameFor(occ), label:labelFor(occ), template:templateFor(occ))
                        }
                    }
                    else {
                        this.builder.node(name:nameFor(occ), label:labelFor(occ), template:templateFor(occ))
                    }
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

    private String templateFor(Referenceable referenceable) {
        switch (referenceable) {
            case Detection.class: return 'detection'
            case Task.class: return 'task'
            case InformationAct.class: return 'infoAct'
            case Agent.class: return 'agent'
            default: return 'event'
        }
    }

    private String nameFor(Referenceable referenceable) {
        return referenceable.id
    }

    private String labelFor(Named named) {
        String label = named.type
        String name = named.name ?: '?'
        if (name.size() > MAX_LABEL_SIZE) name = name.substring(0,MAX_LABEL_SIZE-1)
        label += "\n$name"
        return label
    }

    private String durationToText(Duration duration) {
        Period period = duration.toPeriod()
        String text = ""
        int weeks = period.toStandardWeeks().weeks
        if (weeks) text += "${weeks}W"
        if (period.days) {
            if (text) text += ","
            text += "${period.days}D"
        }
        if (period.hours) {
            if (text) text += ","
            text += "${period.hours}H"
        }
        if (period.minutes) {
            if (text) text += ","
            text += "${period.minutes}M"
        }
        if (period.seconds) {
            if (text) text += ","
            text += "${period.seconds}S"
        }
        return text
    }


}