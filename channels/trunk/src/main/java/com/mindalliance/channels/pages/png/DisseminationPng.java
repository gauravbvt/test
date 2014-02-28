package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.pages.ModelPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 21, 2010
 * Time: 1:38:08 PM
 */
public class DisseminationPng extends DiagramPng {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DisseminationPng.class );
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

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( 
            double[] size, 
            String orientation,
            PageParameters parameters,
            CommunityService communityService,
            DiagramFactory diagramFactory ) throws DiagramException {
        Segment segment = ModelPage.findSegment( communityService.getModelService(), parameters );
        SegmentObject segmentObject = null;
        if ( segment != null && parameters.getNamedKeys().contains( OBJECT ) ) {
            try {
                long id = parameters.get( OBJECT ).toLong();
                try {
                    segmentObject = segment.findFlow( id );
                } catch ( NotFoundException e ) {
                    // do nothing
                }
                if ( segmentObject == null )
                    segmentObject = segment.getNode( id );
            } catch ( Exception ignored ) {
                LOG.warn( "Invalid segment object specified in parameters." );
            }
        }
        boolean showTargets = parameters.get( SHOW_TARGETS ).toBoolean();
        String info = parameters.get( INFO ).toString();
        String content = parameters.get( CONTENT ).toString();
        Subject subject = new Subject( info, content );
        if ( segmentObject == null ) {
            throw new DiagramException( "Missing parameters" );
        } else {
            return diagramFactory.newDisseminationDiagram(
                    segmentObject,
                    subject,
                    showTargets,
                    size,
                    orientation );
        }
    }
}
