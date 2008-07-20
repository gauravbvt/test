package com.mindalliance.channels.playbook.graph

import com.mindalliance.channels.playbook.ifm.playbook.*
import com.mindalliance.channels.playbook.ref.Referenceable
import org.joda.time.Duration
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.support.models.Container
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 4, 2008
 * Time: 1:50:32 PM
 */
abstract class PlaybookGraph extends DirectedGraph {

    static final String LABEL_FONT_SIZE = '7'
    static final String LABEL_FONT_NAME = 'Times-Roman'
    static final String TITLE_FONT_SIZE = '7'
    static final String TITLE_FONT_NAME = 'Helvetica-Bold'
    static final String PROBLEM_FONT_NAME = 'Times-Italic'
    static final String PROBLEM_FONT_SIZE = '7'
    static final String PROBLEM_COLOR = 'red'

    PlaybookGraph(Container container) {
        super(container)
    }

    protected Map getStyleTemplate() {
        return super.getStyleTemplate() + [
                agent: [color: 'lightgray', fillcolor: 'ghostwhite', style: 'filled', fontname: TITLE_FONT_NAME, fontsize:TITLE_FONT_SIZE],
                info: [shape: 'record', fillcolor: 'cornsilk1', style: 'filled, rounded', fontname: LABEL_FONT_NAME, fontsize:LABEL_FONT_SIZE],
                need: [shape: 'record', fillcolor: 'cornsilk2', style: 'filled, rounded', fontname: LABEL_FONT_NAME, fontsize:LABEL_FONT_SIZE],
                infoEdge: [dir: 'none', style: 'dotted', fontname: LABEL_FONT_NAME, fontsize:LABEL_FONT_SIZE],
                transform: [shape: 'trapezium', orientation: '270', fillcolor: 'honeydew2', fontname: LABEL_FONT_NAME, fontsize:LABEL_FONT_SIZE],
                infoAct: [shape: 'diamond', fillcolor: 'lavender', fontname: LABEL_FONT_NAME, fontsize:LABEL_FONT_SIZE],
                task: [shape: 'ellipse', fillcolor: 'azure2', fontname: LABEL_FONT_NAME, fontsize:LABEL_FONT_SIZE],
                detection: [shape: 'egg', fillcolor: 'azure2', fontname: LABEL_FONT_NAME, fontsize:LABEL_FONT_SIZE],
                event: [shape: 'octagon', fillcolor: 'mistyrose', fontname: LABEL_FONT_NAME, fontsize:LABEL_FONT_SIZE],
                problem: [shape: 'plaintext', fontcolor: PROBLEM_COLOR , fontname:PROBLEM_FONT_NAME, fontsize:PROBLEM_FONT_SIZE],
                problemEdge: [dir: 'none', style: 'dotted', color:PROBLEM_COLOR, weight: '10000']
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

    abstract List<Ref> allElements();

    void buildProblems(GraphVizBuilder builder) {
        allElements().each {el ->
            el.problems.each {problem ->
                builder.node(name: nameFor(problem), label: problem.makeLabel(30), URL: urlFor(problem.deref()), template: 'problem')
                builder.edge(source: nameFor(el), target: nameFor(problem), template: 'problemEdge')
            }
        }
    }

    void buildContent(GraphVizBuilder builder) {
        buildProblems(builder)
    }





}