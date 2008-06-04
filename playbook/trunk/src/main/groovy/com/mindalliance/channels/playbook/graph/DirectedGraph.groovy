package com.mindalliance.channels.playbook.graph

import com.mindalliance.channels.playbook.ifm.playbook.Playbook

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 4, 2008
 * Time: 9:19:46 AM
 */
class DirectedGraph {

    static public final int MAX_LABEL_SIZE = 16

    GraphVizBuilder builder
    GraphVizRenderer renderer
    String name = "No name"
    int width = 6
    int height = 4
    Playbook playbook    

    DirectedGraph(Playbook playbook, String[] dimensions) {
        this.playbook = playbook
        width = Integer.valueOf(dimensions[0]) // in inches
        height = Integer.valueOf(dimensions[1]) // in inches
    }

    DirectedGraph(Playbook playbook, int width, int height) {
        this.playbook = playbook
        this.width = width // in inches
        this.height = height // in inches
    }

    GraphVizBuilder getBuilder() {
        if (!builder) {
            renderer = new GraphVizRenderer()
            builder = renderer.getBuilder(getStyleTemplate())
        }
        return builder
    }

    Map getStyleTemplate() {
        Map styleTemplate = [graph: [rankdir: 'LR', fontname: 'Helvetica'],
                node: [fontname: 'Helvetica', fillcolor: 'white', style: 'filled'],
                agent: [color: 'lightgray', fillcolor: 'ghostwhite', style: 'filled', fontname: 'Helvetica-Bold'],
                info: [shape: 'record', fillcolor: 'cornsilk1', style: 'filled, rounded'],
                need: [shape: 'record', fillcolor: 'cornsilk2', style: 'filled, rounded'],
                infoEdge: [dir: 'none', style: 'dotted'],
                transform: [shape: 'trapezium', orientation: '270', fillcolor: 'honeydew2'],
                infoAct: [shape: 'diamond', fillcolor: 'lavender'],
                task: [shape: 'ellipse', fillcolor: 'azure2'],
                detection: [shape: 'egg', fillcolor: 'azure2'],
                event: [shape: 'octagon', fillcolor: 'mistyrose'],
                invisible: [style: 'invisible']
        ]
        return styleTemplate
    }

    String getSvg() {
        build()
        StringWriter writer = new StringWriter()
        renderer.render(writer, "svg")
        return writer.toString()
    }

    void build() {
        this.builder.digraph(name: 'needResolution', size:'6,4', template: 'graph') {
            nodeDefaults(template: 'node')
            buildContent()
        }
    }

    void buildContent() {}

}