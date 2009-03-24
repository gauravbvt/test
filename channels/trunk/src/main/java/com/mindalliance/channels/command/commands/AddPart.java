package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.NotFoundException;

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
        needLockOn( scenario );
        addArgument( "scenario", scenario.getId() );
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
        Service service = commander.getService();
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            // Identify any undefined part likely created to be the lone default part.
            Long defaultPartId = (Long) get( "defaultPart" );
            Part defaultPart = null;
            if ( defaultPartId != null ) {
                // A default part was added before removing the one being restored by adding it.
                if ( scenario.countParts() == 1 && scenario.getDefaultPart().isUndefined()) {
                    defaultPart = scenario.getDefaultPart();
                // commander.mapId( defaultPartId, defaultPart.getId() );
                }
            }
            Part part = service.createPart( scenario );
            commander.mapId( (Long) get( "part" ), part.getId() );
            addArgument( "part", part.getId() );
            if ( defaultPart != null ) scenario.removeNode( defaultPart );
            Map<String, Object> partState = (Map<String, Object>) get( "partState" );
            if ( partState != null ) CommandUtils.initialize( part, partState );
            return new Change( Change.Type.Added, part );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
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
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Long partId = (Long) get( "part" );
            if ( partId == null ) {
                throw new CommandException( "Can't undo." );
            } else {
                Part part = (Part) scenario.getNode( commander.resolveId( partId ) );
                if ( part == null ) throw new NotFoundException();
                return new RemovePart( part );
            }
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo", e );
        }
    }
}
