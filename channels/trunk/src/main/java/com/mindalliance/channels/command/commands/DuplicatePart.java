package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.NotFoundException;

import java.util.Map;

/**
 * Duplicate a part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 3:17:18 PM
 */
public class DuplicatePart extends AbstractCommand {

    public DuplicatePart( Part part ) {
        needLockOn( part.getScenario() );
        needLockOn( part );
        addArgument( "scenario", part.getScenario().getId() );
        addArgument( "part", part.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "duplicate part";
    }

    /**
     * {@inheritDoc}
     */
    public Object execute( Commander commander ) throws CommandException {
        Service service = commander.getService();
        Part duplicate;
        try {
            Scenario scenario = service.find( Scenario.class, (Long) get( "scenario" ) );
            Part part = (Part) scenario.getNode( (Long) get( "part" ) );
            if ( part == null ) throw new NotFoundException();
            Map<String, Object> partState = CommandUtils.getPartState( part );
            duplicate = service.createPart( scenario );
            CommandUtils.initialize( duplicate, partState );
            addArgument( "duplicate", duplicate.getId() );
            return duplicate;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
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
        Service service = commander.getService();
        try {
            Scenario scenario = service.find( Scenario.class, (Long) get( "scenario" ) );
            Long partId = (Long) get( "duplicate" );
            if ( partId == null ) {
                throw new CommandException( "Can't undo." );
            } else {
                Part part = (Part) scenario.getNode( partId );
                return new RemovePart( part );
            }
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo", e );
        }
    }

}
