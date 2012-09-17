/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Change.Type;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;

import java.util.Map;

public class AddNeed extends AbstractCommand {

    public AddNeed() {
        this( "daemon" );
    }

    public AddNeed( String userName ) {
        super( userName );
    }

    public AddNeed( String userName, Part part ) {
        this( userName );
        addConflicting( part );
        set( "part", part.getId() );
        set( "segment", part.getSegment().getId() );
    }

    @Override
    public String getName() {
        return "add information need";
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Part part = (Part) segment.getNode( (Long) get( "part" ) );
            if ( part == null )
                throw new NotFoundException();
            Long priorId = (Long) get( "flow" );
            Long priorConnectorId = (Long) get( "connector" );
            Flow flow = queryService.connect( queryService.createConnector( segment, priorConnectorId ),
                                              part,
                                              (String) get( "name" ),
                                              priorId );
            set( "flow", flow.getId() );
            set( "connector", flow.getSource().getId() );
            Map<String, Object> flowAttributes = (Map<String, Object>) get( "attributes" );
            if ( flowAttributes != null )
                flow.initFromMap( flowAttributes, queryService );

            describeTarget( flow );
            return new Change( Type.Added, flow );
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
            return new RemoveNeed( getUserName(), flow );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
    }
}