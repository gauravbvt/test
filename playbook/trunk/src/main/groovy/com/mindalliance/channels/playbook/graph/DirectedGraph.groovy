package com.mindalliance.channels.playbook.graph

import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.Named
import com.mindalliance.channels.playbook.support.models.Container

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 4, 2008
 * Time: 9:19:46 AM
 */
class DirectedGraph {

    static public final int MAX_LABEL_SIZE = 16

    Container container // the data for the graph

    GraphVizBuilder builder
    GraphVizRenderer renderer
    String name = "No name"
    int width = 0
    int height = 0

    DirectedGraph(Container container) {
        this.container = container
    }

    DirectedGraph(Container container, String[] dimensions) {
        this(container)
        width = Integer.valueOf(dimensions[0]) // in inches
        height = Integer.valueOf(dimensions[1]) // in inches
    }

    DirectedGraph(Container container, int width, int height) {
        this(container)
        this.width = width // in inches
        this.height = height // in inches
    }

    void initBuilder() {
        renderer = new GraphVizRenderer()
        builder = renderer.getBuilder(getStyleTemplate())
    }

    protected Map getStyleTemplate() {
        Map styleTemplate = [
                graph: [rankdir: 'LR', fontname: 'Helvetica',  fontsize:'10'],
                node: [fillcolor: 'white', style: 'filled'],
                invisible: [style: 'invisible']
        ]
        return styleTemplate
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

    String getSvg() {
        build()
        StringWriter writer = new StringWriter()
        renderer.render(writer, "svg")
        String svg = writer.toString()
        int index = svg.indexOf("<svg")
        assert index >=0
        return svg.substring(index)
    }

    void build() {
        initBuilder()
        Map attributes = [name: name, template: this.graphTemplate]
        if (width*height > 0) attributes += [size:"$width,$height"]
        builder.digraph(attributes) {
            nodeDefaults(template: 'node')
            buildContent()
        }
    }

    String getGraphTemplate() {   // DEFAULT
        return 'graph'
    }

    void buildContent() {}

    String urlFor(Referenceable referenceable) {
        return "javascript:svg_wicket_call('__CALLBACK__','${referenceable.id}')"
    }


}