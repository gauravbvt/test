package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.pages.png.DisseminationPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Dissemination diagram panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 10, 2010
 * Time: 8:57:20 PM
 */
public class DisseminationDiagramPanel extends AbstractDiagramPanel {


    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DisseminationDiagramPanel.class );

    /**
     * Part of flow.
     * If flow,  if showTargets, then it's a send, else a receive.
     */
    private IModel<SegmentObject> segmentObjectModel;
    /**
     * The subject being traced.
     */
    private Subject subject;
    /**
     * Dissemination to targets or from sources.
     */
    private boolean showTargets;

    public DisseminationDiagramPanel(
            String id,
            IModel<SegmentObject> segmentObjectModel,
            Subject subject,
            boolean showTargets,
            Settings settings ) {
        super( id,settings );
        this.segmentObjectModel = segmentObjectModel;
        this.subject = subject;
        this.showTargets = showTargets;
        init();
    }

    public static long extractFlowId( String id ) {
        return Long.valueOf( id.substring( id.indexOf( ':' ) + 1 ) );
    }

    /**
     * @inheritDoc
     */
    protected String getContainerId() {
        return "disseminationMap";
    }

    /**
     * @inheritDoc
     */
    protected Diagram makeDiagram() {
        return getDiagramFactory().newDisseminationDiagram(
                getSegmentObject(),
                subject,
                showTargets,
                getDiagramSize(),
                getOrientation() );
    }

    /**
     * @inheritDoc
     */
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        try {
        sb.append( "dissemination.png?" );
        sb.append( DisseminationPage.SEGMENT );
        sb.append( '=' );
        sb.append( getSegmentObject().getSegment().getId() );
        sb.append( '&' );
        sb.append( DisseminationPage.OBJECT );
        sb.append( '=' );
        sb.append( getSegmentObject().getId() );
        sb.append( '&' );
        sb.append( DisseminationPage.INFO );
        sb.append( '=' );
        sb.append( URLEncoder.encode( subject.getInfo(), "UTF-8" ) );
        sb.append( '&' );
        sb.append( DisseminationPage.CONTENT );
        sb.append( '=' );
        sb.append( URLEncoder.encode( subject.getContent(), "UTF-8"  ) );
        sb.append( '&' );
        sb.append( DisseminationPage.SHOW_TARGETS );
        sb.append( '=' );
        sb.append( showTargets );

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
        } catch ( UnsupportedEncodingException e ) {
            // never happens
        }
        sb.append( "&");
        sb.append( TICKET_PARM );
        sb.append( '=' );
        sb.append( getTicket() );
        return sb.toString();
    }

    /**
     * @inheritDoc
     */
    protected void onClick( AjaxRequestTarget target ) {
        // Do nothing
    }

    /**
     * @inheritDoc
     */
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

    /**
     * @inheritDoc
     */
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

    /**
     * @inheritDoc
     */
    protected void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
            AjaxRequestTarget target ) {
        long id = extractFlowId( edgeId );
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

    private SegmentObject getSegmentObject() {
        return segmentObjectModel.getObject();
    }
}
