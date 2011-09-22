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
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.query.QueryService;

import java.util.Map;

/**
 * Duplicate a part.
 */
public class DuplicatePart extends AbstractCommand {

    public DuplicatePart() {
        super( "daemon" );
    }

    public DuplicatePart( String userName, Part part ) {
        super( userName );
        needLockOn( part );
        set( "segment", part.getSegment().getId() );
        set( "part", part.getId() );
    }

    @Override
    public String getName() {
        return "duplicate task";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Part part = (Part) segment.getNode( (Long) get( "part" ) );
            if ( part == null ) throw new NotFoundException();
            Map<String, Object> partState = part.mapState();
            Long priorId = (Long) get( "duplicate" );
            Part duplicate = queryService.createPart( segment, priorId );
            commander.initPartFrom( duplicate, partState );
            set( "duplicate", duplicate.getId() );
            describeTarget( duplicate );                    
            return new Change( Change.Type.Added, duplicate );
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
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Long partId = (Long) get( "duplicate" );
        if ( partId == null )
            throw new CommandException( "Can't undo." );
        else {
            Part part = (Part) segment.getNode( partId );
            return new RemovePart( getUserName(), part );
        }
    }

}
