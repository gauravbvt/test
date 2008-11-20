package com.mindalliance.channels.graph;

import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Flow;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.StringWriter;

/**
 * Builds the graph structure of a scenario
 *
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 3:02:40 PM
 */
public class GraphBuilder {

    Scenario scenario;
    DirectedGraph<Part, Flow> dgraph;

    public GraphBuilder(Scenario scenario) {
        this.scenario = scenario;
    }

    private DirectedGraph<Part, Flow> getDirectedGraph() {
        DirectedGraph<Part, Flow> dgraph = new DefaultDirectedGraph<Part, Flow>(Flow.class);
        // add parts as vertices
        for (Part part : scenario.getParts()) {
            dgraph.addVertex(part);
        }
        // add flows as edges
        // TODO
        return dgraph;
    }

    public String getDot(Part selectedPart) {
        DirectedGraph<Part, Flow> dgraph = getDirectedGraph();
        DOTExporter<Part, Flow> dotExporter = new DOTExporter<Part, Flow>();
        StringWriter writer = new StringWriter();
        dotExporter.export(writer, dgraph);
        return writer.toString();
    }
}
