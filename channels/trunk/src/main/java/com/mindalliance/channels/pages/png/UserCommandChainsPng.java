package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/13/13
 * Time: 2:49 PM
 */
public class UserCommandChainsPng extends DiagramPng {

    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( UserCommandChainsPng.class );

    protected Diagram makeDiagram( double[] size,
                                   String orientation,
                                   PageParameters parameters,
                                   CommunityService communityService,
                                   DiagramFactory diagramFactory ) throws DiagramException {
        ChannelsUser user = null;
        String algo = "dot";
        if ( parameters.getNamedKeys().contains( "user" ) ) {
            String username = parameters.get( "user" ).toString( ChannelsUser.ANONYMOUS_USERNAME );
            user = communityService.getUserRecordService().getUserWithIdentity( username );
        }
        if ( parameters.getNamedKeys().contains( "algo" ) ) {
            algo = parameters.get( "algo" ).toString();
        }
        return diagramFactory.newUserCommandChainsDiagram( user, size, orientation, algo );
    }

}
