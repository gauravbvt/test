package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.RequiredNetworkingGraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.RequirementRelationship;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import org.jgrapht.Graph;

import java.io.OutputStream;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/6/11
 * Time: 3:19 PM
 */
public class RequiredNetworkingDiagram extends AbstractDiagram<Agency, RequirementRelationship> {
    private final Phase.Timing timing;
    private final Event event;
    private final Agency selectedAgency;
    private final RequirementRelationship selectedRequirementRel;

    public RequiredNetworkingDiagram(
            Phase.Timing timing,
            Event event,
            Agency selectedAgency,
            RequirementRelationship selectedRequirementRel,
            double[] diagramSize,
            String orientation ) {
        super( diagramSize, orientation );
        this.timing = timing;
        this.event = event;
        this.selectedAgency = selectedAgency;
        this.selectedRequirementRel = selectedRequirementRel;
    }

    @Override
    public void render(
            String ticket,
            String outputFormat,
            OutputStream outputStream,
            Analyst analyst,
            DiagramFactory diagramFactory,
            CommunityService communityService ) throws DiagramException {
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        RequiredNetworkingGraphBuilder graphBuilder =
                new RequiredNetworkingGraphBuilder( timing, event, selectedAgency, selectedRequirementRel );
        graphBuilder.setCommunityService( communityService );
        Graph<Agency, RequirementRelationship> graph = graphBuilder.buildDirectedGraph();
        GraphRenderer<Agency, RequirementRelationship> graphRenderer = diagramFactory.getGraphRenderer();
        graphRenderer.resetHighlight();
        if ( selectedAgency != null )
            graphRenderer.highlightVertex( selectedAgency );
        if ( selectedRequirementRel != null )
            graphRenderer.highlightEdge( selectedRequirementRel );
        RequiredNetworkingMetaProvider metaProvider = new RequiredNetworkingMetaProvider(
                selectedAgency,
                selectedRequirementRel,
                timing,
                event,
                outputFormat,
                diagramFactory.getImageDirectory(),
                communityService );
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );
        RequiredNetworkingDOTExporter dotExporter = new RequiredNetworkingDOTExporter( metaProvider );
        graphRenderer.render( communityService, graph, dotExporter, outputFormat, ticket, outputStream );
    }
}
