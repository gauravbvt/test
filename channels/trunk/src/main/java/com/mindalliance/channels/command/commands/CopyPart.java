package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.util.ChannelsUtils;

/**
 * Copy edited part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 20, 2009
 * Time: 1:05:15 PM
 */
public class CopyPart extends AbstractCommand {

    public CopyPart() {
    }

    public CopyPart( Part part ) {
        set( "part", part.getId() );
        set( "segment", part.getSegment().getId() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMemorable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "copy task";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Part part = (Part) segment.getNode( (Long) get( "part" ) );
        commander.setCopy( ChannelsUtils.getPartCopy( part ) );
        return new Change( Change.Type.None );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        return null;
    }

}
