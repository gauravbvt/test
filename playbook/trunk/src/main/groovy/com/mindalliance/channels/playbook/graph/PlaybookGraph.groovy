package com.mindalliance.channels.playbook.graph

import com.mindalliance.channels.playbook.ifm.playbook.*
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.Named
import org.joda.time.Duration
import org.joda.time.Period
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.support.models.Container
import com.mindalliance.channels.playbook.ifm.Timing

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 4, 2008
 * Time: 1:50:32 PM
 */
class PlaybookGraph extends DirectedGraph {

    PlaybookGraph(Container container) {
        super(container)
    }

    protected Map getStyleTemplate() {
        return super.getStyleTemplate() + [
                agent: [color: 'lightgray', fillcolor: 'ghostwhite', style: 'filled', fontname: 'Helvetica-Bold', fontsize:'9'],
                info: [shape: 'record', fillcolor: 'cornsilk1', style: 'filled, rounded', fontsize:'9'],
                need: [shape: 'record', fillcolor: 'cornsilk2', style: 'filled, rounded', fontsize:'9'],
                infoEdge: [dir: 'none', style: 'dotted'],
                transform: [shape: 'trapezium', orientation: '270', fillcolor: 'honeydew2', fontsize:'9'],
                infoAct: [shape: 'diamond', fillcolor: 'lavender', fontsize:'9'],
                task: [shape: 'ellipse', fillcolor: 'azure2', fontsize:'9'],
                detection: [shape: 'egg', fillcolor: 'azure2', fontsize:'9'],
                event: [shape: 'octagon', fillcolor: 'mistyrose', fontsize:'9'],
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

    protected String durationToText(Duration duration) {
        return Timing.asString(duration)
    }


}