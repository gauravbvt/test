package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import org.apache.wicket.request.mapper.parameter.PageParameters;
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
public class PlanMapPng extends DiagramPng {
    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanMapPng.class );
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

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( double[] size, 
                                   String orientation,
                                   PageParameters parameters,
                                   CommunityService communityService,
                                   DiagramFactory diagramFactory ) throws DiagramException {
        ModelEntity group = null;
        boolean groupByPhase = false;
        boolean groupByEvent = true;
        Segment segment = null;
        SegmentRelationship sgRel = null;
        PlanService planService = communityService.getPlanService();
        if ( parameters.getNamedKeys().contains( "groupby" )
                && !parameters.get( "groupby" ).toString().equals( "NONE" ) ) {
            String groupBy = parameters.get( "groupby" ).toString();
            groupByPhase = groupBy.equals( "phase" );
            groupByEvent = groupBy.equals( "event" );
        }
        if ( parameters.getNamedKeys().contains( "group" )
                && !parameters.get( "group" ).toString().equals( "NONE" ) ) {
            Long groupId = Long.valueOf( parameters.get( "group" ).toString() );
            try {
                group = planService.find( ModelEntity.class, groupId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Phase or event group not found at :" + groupId, e );
            }
        }
        if ( parameters.getNamedKeys().contains( "segment" )
                && !parameters.get( "segment" ).toString().equals( "NONE" ) ) {
            Long segmentId = Long.valueOf( parameters.get( "segment" ).toString() );
            try {
                segment = planService.find( Segment.class, segmentId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Segment not found at :" + segmentId, e );
            }
        }
        if ( parameters.getNamedKeys().contains( "connection" )
                && !parameters.get( "connection" ).toString().equals( "NONE" ) ) {
            Long scRelId = Long.valueOf( parameters.get( "connection" ).toString() );
            sgRel = new SegmentRelationship();
            sgRel.setId( scRelId, communityService, communityService.getAnalyst() );
        }
        List<Segment> allSegments = planService.list( Segment.class );
        return diagramFactory.newPlanMapDiagram(
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
