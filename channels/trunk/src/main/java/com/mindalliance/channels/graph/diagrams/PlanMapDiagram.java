package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.analysis.network.ScenarioRelationship;
import com.mindalliance.channels.analysis.network.PlanMapGraphBuilder;

import java.io.OutputStream;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

/**
 * A plan map diagram.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 1:58:47 PM
 */
public class PlanMapDiagram extends AbstractDiagram<Scenario,ScenarioRelationship> {

    private List<Scenario> scenarios;

    private Scenario selectedScenario;

    public PlanMapDiagram( List<Scenario> scenarios,
                           DiagramFactory<Scenario, ScenarioRelationship> diagramFactory ) {
        super(diagramFactory);
        this.scenarios = scenarios;
    }

    public void render( String outputFormat, OutputStream outputStream ) {
        DiagramFactory<Scenario,ScenarioRelationship> diagramFactory = getDiagramFactory();
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        GraphBuilder<Scenario, ScenarioRelationship> planMapGraphBuilder =
                new PlanMapGraphBuilder( scenarios, getDiagramFactory().getDqo() );
        Graph<Scenario,ScenarioRelationship> graph =
                planMapGraphBuilder.buildDirectedGraph();
        GraphRenderer<Scenario,ScenarioRelationship> graphRenderer =
                diagramFactory.getGraphRenderer();
        graphRenderer.resetHighlight();
        if ( selectedScenario != null ) {
            graphRenderer.highlightVertex( selectedScenario );
        }
        PlanMapMetaProvider metaProvider = new PlanMapMetaProvider(
                scenarios,
                outputFormat,
                diagramFactory.getImageDirectory(),
                Project.analyst() );
        if ( diagramSize != null ) {
            metaProvider.setGraphSize( diagramSize );
        }
        if ( orientation != null ) {
            metaProvider.setGraphOrientation( orientation );
        }
        PlanMapDOTExporter dotExporter = new PlanMapDOTExporter( metaProvider );
        graphRenderer.render( graph,
                dotExporter,
                outputFormat,
                outputStream
        );
    }

}
