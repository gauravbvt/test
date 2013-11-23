/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.ChecklistsMapGraphBuilder;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;

public class ChecklistsMapDiagram extends AbstractDiagram<Assignment, Commitment> {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ChecklistsMapDiagram.class );

    private Segment segment;

    private final boolean summarizeByOrgType;

    private boolean summarizeByOrg;

    private boolean summarizeByRole;

    private ModelEntity focusEntity;

    public ChecklistsMapDiagram( Segment segment, boolean summarizeByOrgType, boolean summarizeByOrg,
                                 boolean summarizeByRole, ModelEntity focusEntity, double[] diagramSize,
                                 String orientation ) {
        super( diagramSize, orientation );
        this.segment = segment;
        this.summarizeByOrgType = summarizeByOrgType;
        this.summarizeByOrg = summarizeByOrg;
        this.summarizeByRole = summarizeByRole;
        this.focusEntity = focusEntity;
    }

    @Override
    public void render( String ticket, String outputFormat, OutputStream outputStream, Analyst analyst,
                        DiagramFactory diagramFactory, CommunityService communityService ) throws DiagramException {

        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        PlanService planService = communityService.getPlanService();
        ChecklistsMapGraphBuilder graphBuilder =
                new ChecklistsMapGraphBuilder( segment, summarizeByOrgType, summarizeByOrg, summarizeByRole, focusEntity );
        graphBuilder.setCommunityService( communityService );
        Graph<Assignment, Commitment> graph = graphBuilder.buildDirectedGraph();
        GraphRenderer<Assignment, Commitment> graphRenderer = diagramFactory.getGraphRenderer();
        ChecklistsMapMetaProvider metaProvider = new ChecklistsMapMetaProvider( segment,
                                                                          outputFormat,
                                                                          diagramFactory.getImageDirectory(),
                                                                          analyst,
                communityService );
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );
        ChecklistsMapDOTExporter dotExporter = new ChecklistsMapDOTExporter( metaProvider );
        graphRenderer.render( communityService, graph, dotExporter, outputFormat, ticket, outputStream );
    }
}
