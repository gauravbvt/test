package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Segment;
import org.apache.wicket.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Generation of the plan map PNG.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 3, 2009
 * Time: 4:26:10 PM
 */
public class PlanMapPage extends PngWebPage {
    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanMapPage.class );
    /**
     * The selected phase or event.
     */
    private ModelEntity group;
    /**
     * Group segments by phase.
     */
    private boolean groupByPhase;
    /**
     * Group segments by event.
     */
    private boolean groupByEvent;
    /**
     * The selected segments.
     */
    private Segment segment;
    /**
     * The selected segment relationship.
     */
    private SegmentRelationship sgRel;
    /**
     * All segments in plan.
     */
    private List<Segment> allSegments;

    public PlanMapPage( PageParameters parameters ) {
        super( parameters );
        QueryService queryService = getQueryService();
        if ( parameters.containsKey( "groupby" ) && !parameters.getString( "groupby" ).equals( "NONE" ) ) {
            String groupBy = parameters.getString( "groupby" );
            groupByPhase = groupBy.equals( "phase" );
            groupByEvent = groupBy.equals( "event" );
        }
        if ( parameters.containsKey( "group" ) && !parameters.getString( "group" ).equals( "NONE" ) ) {
            Long groupId = Long.valueOf( parameters.getString( "group" ) );
            try {
                group = queryService.find( ModelEntity.class, groupId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Phase or event group not found at :" + groupId, e );
            }
        }
        if ( parameters.containsKey( "segment" ) && !parameters.getString( "segment" ).equals( "NONE" ) ) {
            Long segmentId = Long.valueOf( parameters.getString( "segment" ) );
            try {
                segment = queryService.find( Segment.class, segmentId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Segment not found at :" + segmentId, e );
            }
        }
        if ( parameters.containsKey( "connection" ) && !parameters.getString( "connection" ).equals( "NONE" ) ) {
            Long scRelId = Long.valueOf( parameters.getString( "connection" ) );
            sgRel = new SegmentRelationship();
            sgRel.setId( scRelId, getQueryService() );
        }
        allSegments = queryService.list( Segment.class );
    }

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( double[] size, String orientation ) throws DiagramException {
        return getDiagramFactory().newPlanMapDiagram(
                allSegments,
                groupByPhase,
                groupByEvent,
                group,
                segment,
                sgRel,
                size,
                orientation );
    }
}
