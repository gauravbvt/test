package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.model.Subject;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 21, 2010
 * Time: 1:38:08 PM
 */
public class DisseminationPage extends PngWebPage {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DisseminationPage.class );
    /**
     * Parameter.
     */
    public static final String SEGMENT = "segment";
    /**
     * Parameter.
     */
    public static final String OBJECT = "object";
    /**
     * Parameter.
     */
    public static final String INFO = "info";
    /**
     * Parameter.
     */
    public static final String CONTENT = "content";
    /**
     * Parameter.
     */
    public static final String SHOW_TARGETS = "showTargets";
    private SegmentObject segmentObject;
    private boolean showTargets;
    private Subject subject;

    public DisseminationPage( PageParameters parameters ) {
        super( parameters );
        QueryService queryService = getQueryService();
        Segment segment = PlanPage.findSegment( queryService, parameters );
        if ( segment != null && parameters.containsKey( OBJECT ) ) {
            try {
                long id = parameters.getLong( OBJECT );
                try {
                    segmentObject = segment.findFlow( id );
                } catch ( NotFoundException e ) {
                    // do nothing
                }
                if ( segmentObject == null )
                    segmentObject = segment.getNode( id );
            } catch ( Exception ignored ) {
                LOG.warn( "Invalid failed segment object specified in parameters." );
            }
        }
        showTargets = parameters.getAsBoolean( SHOW_TARGETS );
        String info = parameters.getString( INFO );
        String content = parameters.getString( CONTENT );
        subject = new Subject( info, content );
    }

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( double[] size, String orientation ) throws DiagramException {
        if ( segmentObject == null || subject == null ) {
            throw new DiagramException( "Missing parameters" );
        } else {
            return getDiagramFactory().newDisseminationDiagram(
                    segmentObject,
                    subject,
                    showTargets,
                    size,
                    orientation );
        }
    }
}
