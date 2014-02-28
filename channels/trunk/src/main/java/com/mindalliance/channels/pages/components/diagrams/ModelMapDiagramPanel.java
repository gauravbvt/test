package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.graph.diagrams.ModelMapDiagram;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Plan map diagram panel.
 * Ajax-enabled.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2009
 * Time: 5:36:00 PM
 */
public class ModelMapDiagramPanel extends AbstractDiagramPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ModelMapDiagramPanel.class );

    /**
     * Plan manager.
     */
    @SpringBean
    private ModelManager modelManager;

    /**
     * List of segments to be mapped.
     */
    private List<Segment> segments;
    /**
     * Selected phase or event.
     */
    private ModelObject selectedGroup;
    /**
     * Selected segment.
     */
    private Segment selectedSegment;

    /**
     * Selected segment releationship.
     */
    private SegmentRelationship selectedSgRel;

    /** URL provider for imagemap links. */
    private URLProvider<Segment, SegmentRelationship> uRLProvider;
    /**
     * Whether to group by phase.
     */
    private boolean groupByPhase;
    /**
     * Whether to group by event.
     */
    private boolean groupByEvent;

    public ModelMapDiagramPanel(
            String id,
            IModel<List<Segment>> model,
            boolean groupByPhase,
            boolean groupByEvent,
            ModelObject selectedGroup,
            Segment selectedSegment,
            SegmentRelationship selectedSgRel,
            Settings settings ) {

        this( id, model, groupByPhase, groupByEvent, selectedGroup, selectedSegment, selectedSgRel, null, settings );
    }

    public ModelMapDiagramPanel(
            String id,
            IModel<List<Segment>> model,
            boolean groupByPhase,
            boolean groupByEvent,
            ModelObject selectedGroup,
            Segment selectedSegment,
            SegmentRelationship selectedSgRel,
            URLProvider<Segment, SegmentRelationship> uRLProvider,
            Settings settings ) {

        super( id, settings );
        this.groupByPhase = groupByPhase;
        this.groupByEvent = groupByEvent;
        segments = model.getObject();
        this.selectedGroup = selectedGroup;
        this.selectedSegment = selectedSegment;
        this.selectedSgRel = selectedSgRel;
        this.uRLProvider = uRLProvider;
        init();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected Diagram makeDiagram() {

        ModelMapDiagram diagram = new ModelMapDiagram( segments,
                                                     groupByPhase,
                                                     groupByEvent,
                                                     selectedGroup,
                selectedSegment,
                selectedSgRel,
                                                     getDiagramSize(),
                                                     getOrientation() );
        diagram.setURLProvider( getURLProvider() );
        return diagram;
    }

    /**
     * Overridable imagemap link provider.
     * @return a link provider, or null for the default one.
     */
    public URLProvider<Segment, SegmentRelationship> getURLProvider() {
        return uRLProvider;
    }

    public void setURLProvider( URLProvider<Segment, SegmentRelationship> uRLProvider ) {
        this.uRLProvider = uRLProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getContainerId() {
        return "model-map";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "model.png?" );
        sb.append("groupby=");
        sb.append( groupByPhase
                    ? "phase"
                    : groupByEvent
                        ? "event"
                        : "NONE");
        sb.append("&group=");
        sb.append( selectedGroup == null ? "NONE" : selectedGroup.getId() );
        sb.append("&segment=");
        sb.append( selectedSegment == null ? "NONE" : selectedSegment.getId() );
        sb.append( "&connection=" );
        sb.append( selectedSgRel == null ? "NONE" : selectedSgRel.getId() );
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

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onClick( AjaxRequestTarget target ) {
        update( target, new Change( Change.Type.Selected, ChannelsUser.plan() ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
            AjaxRequestTarget target ) {
        String js = scroll( domIdentifier, scrollTop, scrollLeft );
        try {
            ModelObject group = getQueryService().find( ModelObject.class, Long.valueOf( graphId ));
            Change change = new Change( Change.Type.Selected, group );
            change.setScript( js );
            update( target, change );
        } catch ( NotFoundException e ) {
            LOG.warn( "Nout found", e );
        }
    }

    /**
     * {@inheritDoc}
     */
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
            String js = scroll( domIdentifier, scrollTop, scrollLeft );
            Segment segment = getQueryService().find( Segment.class, Long.valueOf( vertexId ) );
            Change change = new Change( Change.Type.Selected, segment );
            change.setScript( js );
            update( target, change );
        } catch ( NotFoundException e ) {
            LOG.warn( "Not found", e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
            AjaxRequestTarget target ) {
        SegmentRelationship scRel = new SegmentRelationship();
        scRel.setId( Long.valueOf( edgeId ), getCommunityService(), getAnalyst() );
        String js = scroll( domIdentifier, scrollTop, scrollLeft );
        Change change = new Change( Change.Type.Selected, scRel );
        change.setScript( js );
        update( target, change );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String makeSeed() {
        // Force regeneration
        return getCollaborationModel().isDevelopment() ? "&_modified=" + System.currentTimeMillis() : "";
    }

}
