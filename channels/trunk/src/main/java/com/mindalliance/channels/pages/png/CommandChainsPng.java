package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
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
public class CommandChainsPng extends DiagramPng {

    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( CommandChainsPng.class );

    protected Diagram makeDiagram( double[] size,
                                   String orientation,
                                   PageParameters parameters,
                                   CommunityService communityService,
                                   DiagramFactory diagramFactory ) throws DiagramException {
        ChannelsUser user = null;
        Agent agent = null;
        String algo = "dot";
        if ( parameters.getNamedKeys().contains( "user" ) ) {
            String username = parameters.get( "user" ).toString( ChannelsUser.ANONYMOUS_USERNAME );
            user = communityService.getUserRecordService().getUserWithIdentity( username );
        } else if ( parameters.getNamedKeys().contains( "agent" ) && parameters.getNamedKeys().contains( "org" ) ) {
            try {
                long actorId = parameters.get( "agent" ).toLong();
                Actor actor = communityService.find( Actor.class, actorId );
                String registeredOrgUid = parameters.get( "org" ).toString();
                agent = new Agent( actor, registeredOrgUid, communityService );
            } catch ( Exception e ) {
                LOG.error( "Invalid parameters", e );
                throw new DiagramException( "Invalid parameters", e );
            }
        } else {
            LOG.error( "Invalid parameters" );
            throw new DiagramException( "Invalid parameters" );
        }
        if ( parameters.getNamedKeys().contains( "algo" ) ) {
            algo = parameters.get( "algo" ).toString();
        }
        return user != null
                    ? diagramFactory.newCommandChainsDiagram( user, size, orientation, algo )
                    : diagramFactory.newCommandChainsDiagram( agent, size, orientation, algo );
    }

}
