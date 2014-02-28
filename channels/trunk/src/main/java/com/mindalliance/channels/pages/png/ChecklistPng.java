package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.pages.ModelPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/15/13
 * Time: 7:53 AM
 */
public class ChecklistPng extends DiagramPng {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ChecklistPng.class );

    @Override
    protected Diagram makeDiagram( double[] diagramSize,
                                   String orientation,
                                   PageParameters parameters,
                                   CommunityService communityService,
                                   DiagramFactory diagramFactory ) throws DiagramException {
        Segment segment = ModelPage.findSegment( communityService.getModelService(), parameters );
        Part part = null;
        boolean interactive = !parameters.get( "interactive" ).isNull();
        if ( segment != null ) {
            try {
            part = ModelPage.findPart( segment, parameters );

            } catch ( Exception ignored ) {
                LOG.warn( "Invalid part specified in parameters." );
            }
        }
        if ( part == null ) {
            throw new DiagramException( "Missing or invalid parameters" );
        } else {
            return diagramFactory.newChecklistFlowDiagram(
                    part,
                    diagramSize,
                    orientation,
                    interactive
            );
        }
    }


}
