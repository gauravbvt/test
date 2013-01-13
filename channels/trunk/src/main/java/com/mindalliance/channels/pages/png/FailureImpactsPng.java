package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.pages.PlanPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PNG view of an essential flow map.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 20, 2010
 * Time: 7:56:20 PM
 */
public class FailureImpactsPng extends DiagramPng {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( FailureImpactsPng.class );
    /**
     * Parameter.
     */
    public static final String SEGMENT = "segment";
    /**
     * Parameter.
     */
    public static final String FAILURE = "failure";
    /**
     * Parameter.
     */
    public static final String ASSUME_FAILS = "assume_fails";


    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( double[] size,
                                   String orientation,
                                   PageParameters parameters,
                                   PlanCommunity planCommunity,
                                   DiagramFactory diagramFactory ) throws DiagramException {
        SegmentObject segmentObject = null;
        boolean assumeFails;
        Segment segment = PlanPage.findSegment( planCommunity.getPlanService(), parameters );
        if ( segment != null && parameters.getNamedKeys().contains( FAILURE ) ) {
            try {
                long id = parameters.get( FAILURE ).toLong();
                try {
                    segmentObject = segment.findFlow( id );
                } catch ( NotFoundException e ) {
                    // ignore
                }
                if ( segmentObject == null )
                    segmentObject = segment.getNode( id );
            } catch ( Exception ignored ) {
                LOG.warn( "Invalid failed segment object specified in parameters." );
            }
        }
        assumeFails = parameters.get( ASSUME_FAILS).toBoolean( false );
       if ( segmentObject == null )
            throw new DiagramException( "Can't find failed segment object" );
        else
            return diagramFactory.newEssentialFlowMapDiagram(
                    segmentObject,
                    assumeFails,
                    size,
                    orientation );
    }
}
