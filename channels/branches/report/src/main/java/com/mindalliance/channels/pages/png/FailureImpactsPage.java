package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.PageParameters;
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
public class FailureImpactsPage extends PngWebPage {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( FailureImpactsPage.class );
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
     * The hypothetical failure.
     */
    private SegmentObject segmentObject;
    /**
     * WHether alternate flows assumed to fail.
     */
    private boolean assumeFails;

    public FailureImpactsPage( PageParameters parameters ) {
        super( parameters );
        QueryService queryService = getQueryService();
        Segment segment = PlanPage.findSegment( queryService, parameters );
        if ( segment != null && parameters.containsKey( FAILURE ) ) {
            try {
                long id = parameters.getLong( FAILURE );
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
        assumeFails = parameters.getAsBoolean( ASSUME_FAILS, false );
    }

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( double[] size, String orientation ) throws DiagramException {
        if ( segmentObject == null )
            throw new DiagramException( "Can't find failed segment object" );
        else
            return getDiagramFactory().newEssentialFlowMapDiagram(
                    segmentObject,
                    assumeFails,
                    size,
                    orientation );
    }
}
