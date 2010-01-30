package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.graph.diagrams.PlanMapDiagram;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Segment;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Plan map diagram panel.
 * Ajax-enabled.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2009
 * Time: 5:36:00 PM
 */
public class PlanMapDiagramPanel extends AbstractDiagramPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanMapDiagramPanel.class );

    /**
     * Plan manager.
     */
    @SpringBean
    private PlanManager planManager;
    
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

    public PlanMapDiagramPanel(
            String id,
            IModel<ArrayList<Segment>> model,
            boolean groupByPhase,
            boolean groupByEvent,
            ModelObject selectedGroup,
            Segment selectedSegment,
            SegmentRelationship selectedSgRel,
            Settings settings ) {

        this( id, model, groupByPhase, groupByEvent, selectedGroup, selectedSegment, selectedSgRel, null, settings );
    }

    public PlanMapDiagramPanel(
            String id,
            IModel<ArrayList<Segment>> model,
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

        PlanMapDiagram diagram = new PlanMapDiagram( segments,
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
        return "plan-map";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "/plan.png?" );
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
        return sb.toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onClick( AjaxRequestTarget target ) {
        update( target, new Change( Change.Type.Selected, planManager.getCurrentPlan() ) );
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
            AjaxRequestTarget target ) {
        try {
            String js = scroll( domIdentifier, scrollTop, scrollLeft );
            Segment segment = getQueryService().find( Segment.class, Long.valueOf( vertexId ) );
            Change change = new Change( Change.Type.Selected, segment );
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
    protected void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        SegmentRelationship scRel = new SegmentRelationship();
        scRel.setId( Long.valueOf( edgeId ), getQueryService() );
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
        return "&_modified=" + System.currentTimeMillis();
    }

}
