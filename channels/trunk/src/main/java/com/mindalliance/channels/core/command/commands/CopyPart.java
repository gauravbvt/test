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
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.ChannelsUtils;

/**
 * Copy edited part.
 */
public class CopyPart extends AbstractCommand {

    public CopyPart() {
        super( "daemon" );
    }

    public CopyPart( String userName, Part part ) {
        super( userName );
        set( "part", part.getId() );
        set( "segment", part.getSegment().getId() );
        needLockOn( part );
    }

    @Override
    public boolean isMemorable() {
        return false;
    }

    @Override
    public String getName() {
        return "copy task";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Part part = (Part) segment.getNode( (Long) get( "part" ) );
        commander.setCopy( getUserName(), ChannelsUtils.getPartCopy( part ) );
        Change change = new Change( Type.None );
        change.setMessage( "Task copied" );
        return change;
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
