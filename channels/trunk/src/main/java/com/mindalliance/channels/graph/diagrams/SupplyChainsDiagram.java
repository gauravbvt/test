package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.AssetSupplyCommitment;
import com.mindalliance.channels.engine.analysis.graph.SupplyChainsGraphBuilder;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import org.jgrapht.Graph;

import java.io.OutputStream;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/3/14
 * Time: 9:13 PM
 */
public class SupplyChainsDiagram extends AbstractDiagram<Assignment,AssetSupplyCommitment> {

    private MaterialAsset materialAsset;
    private final boolean summarizeByOrgType;

    private boolean summarizeByOrg;

    private boolean summarizeByRole;

    public SupplyChainsDiagram( MaterialAsset materialAsset,
                                boolean summarizeByOrgType,
                                boolean summarizeByOrg,
                                boolean summarizeByRole,
                                double[] diagramSize, String orientation ) {
        super( diagramSize, orientation );
        this.materialAsset = materialAsset;
        this.summarizeByOrgType = summarizeByOrgType;
        this.summarizeByOrg = summarizeByOrg;
        this.summarizeByRole = summarizeByRole;
    }

    @Override
    public void render( String ticket,
                        String outputFormat,
                        OutputStream outputStream,
                        Analyst analyst,
                        DiagramFactory diagramFactory,
                        CommunityService communityService ) throws DiagramException {
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        SupplyChainsGraphBuilder graphBuilder =
                new SupplyChainsGraphBuilder( materialAsset, summarizeByOrgType, summarizeByOrg, summarizeByRole );
        graphBuilder.setCommunityService( communityService );
        Graph<Assignment, AssetSupplyCommitment> graph = graphBuilder.buildDirectedGraph();
        GraphRenderer<Assignment, AssetSupplyCommitment> graphRenderer = diagramFactory.getGraphRenderer();
        SupplyChainsMetaProvider metaProvider = new SupplyChainsMetaProvider( materialAsset,
                outputFormat,
                diagramFactory.getImageDirectory(),
                analyst,
                communityService );
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );
        SupplyChainsDOTExporter dotExporter = new SupplyChainsDOTExporter( metaProvider );
        graphRenderer.render( communityService, graph, dotExporter, outputFormat, ticket, outputStream );
    }
}
