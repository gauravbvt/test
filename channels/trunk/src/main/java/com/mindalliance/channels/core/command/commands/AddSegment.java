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
import com.mindalliance.channels.core.model.Segment;

/**
 * Command to add a segment.
 */
public class AddSegment extends AbstractCommand {

    public AddSegment() {
        this( "daemon" );
    }

    public AddSegment( String userName ) {
        super( userName );
    }

    @Override
    public String getName() {
        return "add new segment";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        Long priorId = (Long) get( "segment" );
        Long priorDefaultPartId = (Long) get( "defaultPart" );
        Segment segment = commander.getQueryService().createSegment( priorId, priorDefaultPartId );
        segment.addOwner( getUserName() );
        commander.getPlan().addSegment( segment );
        set( "segment", segment.getId() );
        set( "defaultPart", segment.getDefaultPart().getId() );
        describeTarget( segment );
        return new Change( Type.Added, segment );
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        return null;
    }
}
