package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.ChecklistElement;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.ChecklistElementRelationship;
import com.mindalliance.channels.engine.analysis.graph.ChecklistFlowGraphBuilder;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import org.jgrapht.DirectedGraph;
import org.springframework.core.io.Resource;

import java.io.OutputStream;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/13/13
 * Time: 8:50 PM
 */
public class ChecklistFlowDiagram extends AbstractDiagram<ChecklistElement, ChecklistElementRelationship> {

    private final Part part;

    public ChecklistFlowDiagram( Part part, double[] diagramSize, String orientation ) {
        super(diagramSize, orientation );
        this.part = part;
    }

    @Override
    public void render( String ticket,
                        String outputFormat,
                        OutputStream outputStream,
                        Analyst analyst,
                        DiagramFactory diagramFactory,
                        CommunityService communityService ) throws DiagramException {
        GraphRenderer<ChecklistElement, ChecklistElementRelationship> renderer = diagramFactory.getGraphRenderer();
        PlanService planService = communityService.getPlanService();
        renderer.render( communityService,
                createGraph( planService, analyst ),
                createExporter( outputFormat, diagramFactory.getImageDirectory(), analyst, planService ),
                outputFormat,
                ticket,
                outputStream );
    }

    private DirectedGraph<ChecklistElement, ChecklistElementRelationship> createGraph( PlanService planService,
                                                                                       Analyst analyst ) {
        return new ChecklistFlowGraphBuilder( part, planService, analyst ).buildDirectedGraph();
    }

    private ChecklistFlowDOTExporter createExporter( String outputFormat,
                                                     Resource imageDirectory,
                                                     Analyst analyst,
                                                     PlanService planService ) {
        ChecklistFlowMetaProvider metaProvider = new ChecklistFlowMetaProvider( part, outputFormat, imageDirectory, analyst, planService );
        double[] diagramSize = getDiagramSize();
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );

        String orientation = getOrientation();
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );
        return new ChecklistFlowDOTExporter( metaProvider, part.getChecklist() );
    }
}
