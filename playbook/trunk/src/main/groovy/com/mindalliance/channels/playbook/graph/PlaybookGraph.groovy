package com.mindalliance.channels.playbook.graph

import com.mindalliance.channels.playbook.ifm.playbook.*
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.Named
import org.joda.time.Duration
import org.joda.time.Period
import com.mindalliance.channels.playbook.ifm.Agent

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 4, 2008
 * Time: 1:50:32 PM
 */
class PlaybookGraph extends DirectedGraph {

    Playbook playbook

    PlaybookGraph(Playbook playbook) {
        super()
        this.playbook = playbook
    }

    PlaybookGraph(Playbook playbook, String[] dimensions) {
        super(dimensions)
        this.playbook = playbook
    }

    PlaybookGraph(Playbook playbook, int width, int height) {
        super(width, height)
        this.playbook = playbook
    }

    protected Map getStyleTemplate() {
        return super.getStyleTemplate() + [
                agent: [color: 'lightgray', fillcolor: 'ghostwhite', style: 'filled', fontname: 'Helvetica-Bold', fontsize:'10'],
                info: [shape: 'record', fillcolor: 'cornsilk1', style: 'filled, rounded', fontsize:'10'],
                need: [shape: 'record', fillcolor: 'cornsilk2', style: 'filled, rounded', fontsize:'10'],
                infoEdge: [dir: 'none', style: 'dotted'],
                transform: [shape: 'trapezium', orientation: '270', fillcolor: 'honeydew2', fontsize:'10'],
                infoAct: [shape: 'diamond', fillcolor: 'lavender', fontsize:'10'],
                task: [shape: 'ellipse', fillcolor: 'azure2', fontsize:'10'],
                detection: [shape: 'egg', fillcolor: 'azure2', fontsize:'10'],
                event: [shape: 'octagon', fillcolor: 'mistyrose', fontsize:'10'],
        ]
    }

    protected String templateFor(Referenceable referenceable) {
        switch (referenceable) {
            case Detection.class: return 'detection'
            case Task.class: return 'task'
            case InformationAct.class: return 'infoAct'
            case Agent.class: return 'agent'
            default: return 'event'
        }
    }

    protected String nameFor(Referenceable referenceable) {
        return referenceable.id.replaceAll("-","")
    }

    protected String labelFor(Named named) {
        String label = named.type
        String name = named.name ?: '?'
        if (name.size() > MAX_LABEL_SIZE) name = name.substring(0, MAX_LABEL_SIZE - 1)
        label += "\n$name"
        return label
    }

    protected String durationToText(Duration duration) {
        long seconds = duration.millis / 1000 as long
        long minutes = seconds / 60 as long
        long hours = minutes / 60 as long
        long days = hours / 24 as long
        long weeks = days / 7 as long
        String text = ""
        if (weeks) text += "${weeks}w"
        if (days % 7) {
            if (text) text += " "
            text += "${days % 7}d"
        }
        if (hours % 24) {
            if (text) text += " "
            text += "${hours % 24}h"
        }
        if (minutes % 60) {
            if (text) text += " "
            text += "${minutes % 60}m"
        }
        if (seconds % 60) {
            if (text) text += " "
            text += "${seconds % 60}s"
        }
        if (!text) {
            text = "Start"
        }
        else {
            text = "+$text"
        }
        return text
    }


}