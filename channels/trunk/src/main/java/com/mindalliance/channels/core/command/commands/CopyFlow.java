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
import com.mindalliance.channels.core.util.ChannelsUtils;

/**
 * Copy a flow.
 */
public class CopyFlow extends AbstractCommand {

    public CopyFlow() {
        super( "daemon" );
    }

    public CopyFlow( String userName, Flow flow, Part part ) {
        super( userName );
        set( "part", part.getId() );
        set( "flow", flow.getId() );
        set( "segment", flow.getSegment().getId() );
        needLockOn( flow );
    }

    @Override
    public boolean isMemorable() {
        return false;
    }

    @Override
    public String getName() {
        return "copy";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Flow flow;
        Part part;
        try {
            part = (Part) segment.getNode( (Long) get( "part" ) );
            flow = segment.findFlow( (Long) get( "flow" ) );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
        commander.setCopy( getUserName(), flow.getTarget() == part ?
                           ChannelsUtils.getReceiveState( flow, part ) :
                           ChannelsUtils.getSendState( flow, part ) );
        Change change = new Change( Type.None, flow );
        change.setMessage( flow.getNature() + " copied" );
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

    @Override
    public String getLabel( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Flow flow;
        try {
            flow = segment.findFlow( (Long) get( "flow" ) );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh" );
        }
        return flow.isCapability() ? "Copy capability"
                                   : flow.isNeed() ? "Copy need" : "Copy flow";
    }
}
