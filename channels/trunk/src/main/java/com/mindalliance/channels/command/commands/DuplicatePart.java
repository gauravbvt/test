package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;

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
        set( "segment", part.getSegment().getId() );
        set( "part", part.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "duplicate task";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Part duplicate;
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Part part = (Part) segment.getNode( (Long) get( "part" ) );
            if ( part == null ) throw new NotFoundException();
            Map<String, Object> partState = part.mapState();
            Long priorId = (Long) get( "duplicate" );
            duplicate = queryService.createPart( segment, priorId );
            commander.initPartFrom( duplicate, partState );
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
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Long partId = (Long) get( "duplicate" );
        if ( partId == null ) {
            throw new CommandException( "Can't undo." );
        } else {
            Part part = (Part) segment.getNode( partId );
            return new RemovePart( part );
        }
    }

}
