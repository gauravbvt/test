package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 11:31:48 AM
 */
public class FlowMapDiagramPanel extends AbstractDiagramPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( FlowMapDiagramPanel.class );
    /**
     * Segment to be diagrammed
     */
    private IModel<Segment> segmentModel;
    /**
     * Selected node. Null if none selected.
     */
    private IModel<Part> partModel;
    /**
     * Whether to show goals.
     */
    private boolean showingGoals;
    /**
     * Whether to show connectors.
     */
    private boolean showingConnectors;

    public FlowMapDiagramPanel( String id,
                                IModel<Segment> segmentModel,
                                IModel<Part> partModel,
                                Settings settings ) {
        this( id, segmentModel, partModel, settings, false, false );
    }

    public FlowMapDiagramPanel( String id,
                                IModel<Segment> segmentModel,
                                IModel<Part> partModel,
                                Settings settings,
                                boolean showingGoals,
                                boolean showingConnectors ) {
        super( id, settings );
        this.segmentModel = segmentModel;
        this.partModel = partModel;
        this.showingGoals = showingGoals;
        this.showingConnectors = showingConnectors;
        init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getContainerId() {
        return "flow-map";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Diagram makeDiagram( ) {
        return getDiagramFactory().newFlowMapDiagram(
                getSegment(),
                getPart(),
                getDiagramSize(),
                getOrientation(),
                showingGoals,
                showingConnectors );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "/segment.png?segment=" );
        sb.append( getSegment().getId() );
        sb.append( "&node=" );
        if ( getPart() != null ) {
            sb.append( getPart().getId() );
        } else {
            sb.append( "NONE" );
        }
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
        if ( showingGoals ) {
            sb.append( "&showingGoals=" );
            sb.append( showingGoals );
        }
        if ( showingConnectors ) {
            sb.append( "&showingConnectors=" );
            sb.append( showingConnectors );
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onClick( AjaxRequestTarget target ) {
        // Do nothing
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        try {
            Segment segment = getQueryService().find( Segment.class, Long.valueOf( graphId ) );
            this.update( target, new Change( Change.Type.Selected, segment ) );
        } catch ( NotFoundException e ) {
            LOG.warn( "Selection not found", e );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onSelectVertex(
            String graphId,
            String vertexId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        try {
            Segment segment = getQueryService().find( Segment.class, Long.valueOf( graphId ) );
            Part part = (Part) segment.getNode( Long.valueOf( vertexId ) );
            if ( part != null ) {
                String js = scroll( domIdentifier, scrollTop, scrollLeft );
                Change change = new Change( Change.Type.Selected, part );
                change.setScript( js );
                this.update( target, change );

            } else {
                throw new NotFoundException();
            }
        } catch ( NotFoundException e ) {
            LOG.warn( "Selection not found", e );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        long id =  Long.valueOf( edgeId );
        try {
            Flow flow = getQueryService().find( Flow.class, id );
            String js = scroll( domIdentifier, scrollTop, scrollLeft );
            Change change = new Change( Change.Type.Selected, flow );
            change.setScript( js );
            update( target, change );
        } catch ( NotFoundException e ) {
            LOG.warn( "Selected flow not found at id " + id );
        }
    }

    private Segment getSegment() {
        return segmentModel.getObject();
    }

    private Part getPart() {
        if ( partModel == null )
            return null;
        else
            return partModel.getObject();
    }
}

