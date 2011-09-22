/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.ProceduresGraphBuilder;
import com.mindalliance.channels.engine.query.QueryService;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Segment;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;

public class ProcedureMapDiagram extends AbstractDiagram<Assignment, Commitment> {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ProcedureMapDiagram.class );

    private Segment segment;

    private final boolean summarizeByOrgType;

    private boolean summarizeByOrg;

    private boolean summarizeByRole;

    private ModelEntity focusEntity;

    public ProcedureMapDiagram( Segment segment, boolean summarizeByOrgType, boolean summarizeByOrg,
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
                        DiagramFactory diagramFactory, QueryService queryService ) {

        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        ProceduresGraphBuilder graphBuilder =
                new ProceduresGraphBuilder( segment, summarizeByOrgType, summarizeByOrg, summarizeByRole, focusEntity );
        graphBuilder.setQueryService( queryService );
        Graph<Assignment, Commitment> graph = graphBuilder.buildDirectedGraph();
        GraphRenderer<Assignment, Commitment> graphRenderer = diagramFactory.getGraphRenderer();
        ProceduresMetaProvider metaProvider = new ProceduresMetaProvider( segment,
                                                                          outputFormat,
                                                                          diagramFactory.getImageDirectory(),
                                                                          analyst,
                                                                          queryService );
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );
        ProceduresDOTExporter dotExporter = new ProceduresDOTExporter( metaProvider );
        graphRenderer.render( queryService, graph, dotExporter, outputFormat, ticket, outputStream );
    }
}
