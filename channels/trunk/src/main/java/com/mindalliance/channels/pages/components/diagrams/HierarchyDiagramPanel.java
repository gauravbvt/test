package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.graph.Diagram;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 25, 2009
 * Time: 3:04:13 PM
 */
public class HierarchyDiagramPanel extends AbstractDiagramPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( HierarchyDiagramPanel.class );
    /**
     * Hierarchical object model.
     */
    private IModel<? extends Hierarchical> hierarchicalModel;
    private final String algo;

    public HierarchyDiagramPanel(
            String id,
            IModel<? extends Hierarchical> hierarchicalModel,
            double[] diagramSize,
            String domIdentifier ) {
         this( id, hierarchicalModel, diagramSize, domIdentifier, "dot" );
    }

    public HierarchyDiagramPanel(
            String id,
            IModel<? extends Hierarchical> hierarchicalModel,
            double[] diagramSize,
            String domIdentifier,
            String algo) {
        super( id, new Settings( domIdentifier, null, diagramSize, true, true ) );
        this.hierarchicalModel = hierarchicalModel;
        this.algo = algo;
        init();
    }

    @Override
    protected String getContainerId() {
        return "hierarchy-diagram";
    }

    @Override
    protected Diagram makeDiagram() {
        return getDiagramFactory().newHierarchyDiagram(
                hierarchicalModel.getObject(),
                getDiagramSize(),
                getOrientation(),
                algo );
    }

    @Override
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "hierarchy.png?entity=" );
        sb.append( getHierarchical().getId() );
        sb.append( "&algo=");
        sb.append( algo );
        double[] diagramSize = getDiagramSize();
        if ( diagramSize != null ) {
            sb.append( "&size=" );
            sb.append( diagramSize[0] );
            sb.append( "," );
            sb.append( diagramSize[1] );
        }
        String orientation = getOrientation();
        if ( orientation != null ) {
            sb.append( "&orientation=" );
            sb.append( orientation );
        }
        sb.append( "&");
        sb.append( TICKET_PARM );
        sb.append( '=' );
        sb.append( getTicket() );
        return sb.toString();
    }

    @Override
    protected void onClick( AjaxRequestTarget target ) {
        // Do nothing
    }

    @Override
    protected void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
            AjaxRequestTarget target ) {
        // Do nothing -- never called
    }

    @Override
    protected void onSelectVertex(
            String graphId,
            String vertexId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
            AjaxRequestTarget target ) {
        try {
            Hierarchical hierarchical = (Hierarchical) getQueryService().find(
                    ModelObject.class,
                    Long.valueOf( vertexId ) );
            if ( hierarchical != getHierarchical() ) {
                // String js = scroll( domIdentifier, scrollTop, scrollLeft );
                Change change = new Change( Change.Type.Selected, hierarchical );
                // change.setScript( js );
                update( target, change );
            }
        } catch ( NotFoundException e ) {
            LOG.warn( "Not found", e );
        }
    }

    @Override
    protected void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
            AjaxRequestTarget target ) {
        // Never called
    }

    private Hierarchical getHierarchical() {
        return hierarchicalModel.getObject();
    }

}
