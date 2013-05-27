/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.Information;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;

import java.util.Map;

public class AddCapability extends AbstractCommand {

    public AddCapability() {
        this( "daemon" );
    }

    public AddCapability( String userName ) {
        super( userName );
    }

    public AddCapability( String userName, Part part ) {
        this( userName );
        addConflicting( part );
        set( "part", part.getId() );
        set( "segment", part.getSegment().getId() );
    }

    public AddCapability( String userName, Part part, Information information ) {
        this( userName );
        addConflicting( part );
        set( "part", part.getId() );
        set( "segment", part.getSegment().getId() );
        set( "information", information.getState() );
    }


    @Override
    public String getName() {
        return "add sharing capability";
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        CommunityService communityService = commander.getCommunityService();
        QueryService queryService = communityService.getPlanService();
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Part part = (Part) segment.getNode( (Long) get( "part" ) );
            if ( part == null )
                throw new NotFoundException();

            // Can be null
            Long priorFlowId = (Long) get( "flow" );
            Long priorConnectorId = (Long) get( "connector" );
            Flow flow = queryService.connect( part,
                                              queryService.createConnector( segment, priorConnectorId ),
                                              (String) get( "name" ),
                                              priorFlowId );
            describeTarget( flow );
            Map<String, Object> flowAttributes = (Map<String, Object>) get( "attributes" );
            if ( flowAttributes != null )
                flow.initFromMap( flowAttributes, communityService );
            Map<String,Object> infoState = (Map<String, Object>) get( "information" );
            if ( infoState != null ) {
                Information info = Information.fromState( infoState, queryService );
                flow.setName( info.getName() );
                InfoProduct infoProduct = info.getInfoProduct();
                if ( infoProduct != null ) {
                    flow.setInfoProduct( info.getInfoProduct() );
                    flow.setStandardized( true );
                }
                flow.setEois( info.getEois() );
            }
            set( "flow", flow.getId() );
            set( "connector", flow.getTarget().getId() );
            return new Change( Change.Type.Added, flow );

        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            return new RemoveCapability( getUserName(), flow );

        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
    }
}
