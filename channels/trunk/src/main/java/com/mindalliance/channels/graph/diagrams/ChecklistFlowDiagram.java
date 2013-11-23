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
    private final boolean interactive;

    public ChecklistFlowDiagram( Part part, double[] diagramSize, String orientation, boolean interactive ) {
        super(diagramSize, orientation );
        this.part = part;
        this.interactive = interactive;
    }

    @Override
    public void render( String ticket,
                        String outputFormat,
                        OutputStream outputStream,
                        Analyst analyst,
                        DiagramFactory diagramFactory,
                        CommunityService communityService ) throws DiagramException {
        GraphRenderer<ChecklistElement, ChecklistElementRelationship> renderer = diagramFactory.getGraphRenderer();
        renderer.render( communityService,
                createGraph( communityService, analyst ),
                createExporter( outputFormat, diagramFactory.getImageDirectory(), analyst, communityService ),
                outputFormat,
                ticket,
                outputStream );
    }

    private DirectedGraph<ChecklistElement, ChecklistElementRelationship> createGraph( CommunityService communityService,
                                                                                       Analyst analyst ) {
        return new ChecklistFlowGraphBuilder( part, communityService, analyst ).buildDirectedGraph();
    }

    private ChecklistFlowDOTExporter createExporter( String outputFormat,
                                                     Resource imageDirectory,
                                                     Analyst analyst,
                                                     CommunityService communityService ) {
        ChecklistFlowMetaProvider metaProvider = new ChecklistFlowMetaProvider(
                part,
                outputFormat,
                imageDirectory,
                analyst,
                communityService,
                interactive );
        double[] diagramSize = getDiagramSize();
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );

        String orientation = getOrientation();
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );
        return new ChecklistFlowDOTExporter( metaProvider, part.getEffectiveChecklist() );
    }
}
