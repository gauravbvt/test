package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.pages.png.FailureImpactsPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Critical flows diagram panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 15, 2010
 * Time: 11:24:11 AM
 */
public class FailureImpactsDiagramPanel extends AbstractDiagramPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( FailureImpactsDiagramPanel.class );
    /**
     * Segment object model.
     */
    private IModel<SegmentObject> segmentObjectModel;
    /**
     * Assume alternate flows to downstream slows all fail?
     */
    private boolean assumeFails;

    public FailureImpactsDiagramPanel(
            String id,
            IModel<SegmentObject> model,
            boolean assumeFails,
            Settings settings ) {
        super( id, settings );
        segmentObjectModel = model;
        this.assumeFails = assumeFails;
        init();
    }

    protected String getContainerId() {
        return "essentialFlowMap";
    }

    protected Diagram makeDiagram() {
        return getDiagramFactory().newEssentialFlowMapDiagram(
                getSegmentObject(),
                assumeFails,
                getDiagramSize(),
                getOrientation() );
    }

    private SegmentObject getSegmentObject() {
        return segmentObjectModel.getObject();
    }

    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "/essential.png?" );
        sb.append( FailureImpactsPage.SEGMENT );
        sb.append( '=' );
        sb.append( getSegmentObject().getSegment().getId() );
        sb.append( '&' );
        sb.append( FailureImpactsPage.FAILURE );
        sb.append( '=' );
        sb.append( getSegmentObject().getId() );
        sb.append( '&' );
        sb.append( FailureImpactsPage.ASSUME_FAILS );
        sb.append( '=' );
        sb.append( assumeFails );
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
        return sb.toString();
    }

    protected void onClick( AjaxRequestTarget target ) {
        // Do nothing
    }

    protected void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
            AjaxRequestTarget target ) {
        try {
            Segment segment = getQueryService().find( Segment.class, Long.valueOf( graphId ) );
            this.update( target, new Change( Change.Type.Selected, segment ) );
        } catch ( NotFoundException e ) {
            LOG.warn( "Selection not found", e );
        }
    }

    protected void onSelectVertex(
            String graphId,
            String vertexId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
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

    protected void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
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
}
