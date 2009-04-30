package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.analysis.graph.PlanMapGraphBuilder;

import java.io.OutputStream;
import java.util.List;

import org.jgrapht.Graph;

/**
 * A plan map diagram.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 1:58:47 PM
 */
public class PlanMapDiagram extends AbstractDiagram<Scenario,ScenarioRelationship> {
    /**
     * The scenarios mapped.
     */
    private List<Scenario> scenarios;
    /**
     * Selected vertex-scenario.
     */
    private Scenario selectedScenario;
    /**
     * Selected edge-scenario relationship.
     */
    private ScenarioRelationship selectedScRel;

    public PlanMapDiagram(
            List<Scenario> scenarios,
            Scenario scenario,
            ScenarioRelationship scRel,
            double[] diagramSize,
            String orientation ) {
        super( diagramSize, orientation );
        this.scenarios = scenarios;
        this.selectedScenario = scenario;
        this.selectedScRel = scRel;
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
        if ( selectedScRel != null ) {
            graphRenderer.highlightEdge( selectedScRel );
        }
        PlanMapMetaProvider metaProvider = new PlanMapMetaProvider(
                scenarios,
                outputFormat,
                diagramFactory.getImageDirectory(),
                Channels.analyst() );
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
