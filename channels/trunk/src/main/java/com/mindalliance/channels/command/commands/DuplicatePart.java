package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.model.Scenario;
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

    public DuplicatePart() {
    }

    public DuplicatePart( Part part ) {
        needLockOn( part.getScenario() );
        needLockOn( part );
        set( "scenario", part.getScenario().getId() );
        set( "part", part.getId() );
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
    public Change execute( Commander commander ) throws CommandException {
        DataQueryObject dqo = commander.getDqo();
        Part duplicate;
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Part part = (Part) scenario.getNode( commander.resolveId( (Long) get( "part" ) ) );
            if ( part == null ) throw new NotFoundException();
            Map<String, Object> partState = CommandUtils.getPartState( part );
            duplicate = dqo.createPart( scenario );
            CommandUtils.initialize( duplicate, partState );
            commander.mapId( (Long) get( "duplicate" ), duplicate.getId() );
            set( "duplicate", duplicate.getId() );
            return new Change( Change.Type.Added, duplicate );
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
    @Override
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
        Long partId = (Long) get( "duplicate" );
        if ( partId == null ) {
            throw new CommandException( "Can't undo." );
        } else {
            Part part = (Part) scenario.getNode( commander.resolveId( partId ) );
            return new RemovePart( part );
        }
    }

}
