package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.util.ChannelsUtils;

import java.util.Map;

/**
 * Command to add a new part to a scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 5, 2009
 * Time: 1:06:46 PM
 */
public class AddPart extends AbstractCommand {

    public AddPart() {
    }

    public AddPart( Scenario scenario ) {
        super();
        set( "scenario", scenario.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "add part";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
        // Identify any undefined part likely created to be the lone default part.
        Long defaultPartId = (Long) get( "defaultPart" );
        Part defaultPart = null;
        if ( defaultPartId != null ) {
            // A default part was added before removing the one being restored by adding it.
            if ( scenario.countParts() == 1 && scenario.getDefaultPart().isUndefined() ) {
                defaultPart = scenario.getDefaultPart();
            }
        }
        Long priorId = (Long) get( "part" );
        Part part = queryService.createPart( scenario, priorId );
        set( "part", part.getId() );
        if ( defaultPart != null ) scenario.removeNode( defaultPart );
        Map<String, Object> partState = (Map<String, Object>) get( "partState" );
        if ( partState != null ) {
            ChannelsUtils.initPartFrom( part, partState, commander );
        }
        return new Change( Change.Type.Added, part );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Part part = (Part) scenario.getNode( (Long) get( "part" ) );
            if ( part == null ) throw new NotFoundException();
            return new RemovePart( part );
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo", e );
        }
    }
}
