package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.analysis.graph.EssentialFlowMapGraphBuilder;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ScenarioObject;
import org.jgrapht.Graph;

import java.io.OutputStream;

/**
 * Essential flow map diagram.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 15, 2010
 * Time: 1:44:56 PM
 */
public class EssentialFlowMapDiagram extends AbstractDiagram<Node, Flow> {

    private ScenarioObject scenarioObject;
    private boolean assumeFails;

    public EssentialFlowMapDiagram(
            ScenarioObject scenarioObject,
            boolean assumeFails,
            double[] diagramSize,
            String orientation ) {
        super( diagramSize, orientation );
        this.scenarioObject = scenarioObject;
        this.assumeFails = assumeFails;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public void render( String outputFormat, OutputStream outputStream ) {
        DiagramFactory<Node, Flow> diagramFactory = getDiagramFactory();
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        GraphBuilder essentialFlowMapGraphBuilder = new EssentialFlowMapGraphBuilder( scenarioObject, assumeFails );
        Graph<Node, Flow> graph = essentialFlowMapGraphBuilder.buildDirectedGraph();
        GraphRenderer<Node, Flow> graphRenderer = diagramFactory.getGraphRenderer();
        graphRenderer.resetHighlight();
        if ( scenarioObject instanceof Part ) {
            graphRenderer.highlightVertex( (Part)scenarioObject );
        } else {
            graphRenderer.highlightEdge( (Flow)scenarioObject );
        }
        FlowMapMetaProvider metaProvider = new FlowMapMetaProvider(
                scenarioObject.getScenario(),
                outputFormat,
                diagramFactory.getImageDirectory(),
                getAnalyst() );
        if ( diagramSize != null ) {
            metaProvider.setGraphSize( diagramSize );
        }
        if ( orientation != null ) {
            metaProvider.setGraphOrientation( orientation );
        }
        EssentialFlowMapDOTExporter dotExporter = new EssentialFlowMapDOTExporter( metaProvider );
        graphRenderer.render( graph,
                dotExporter,
                outputFormat,
                outputStream
        );

    }


}
