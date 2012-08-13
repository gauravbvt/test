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
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.ChannelsUtils;

public class RemoveNeed extends AbstractCommand {

    public RemoveNeed() {
        super( "daemon" );
    }

    public RemoveNeed( String userName, Flow flow ) {
        super( userName );
        needLocksOn( ChannelsUtils.getLockingSetFor( flow ) );
        setArguments( ChannelsUtils.getFlowConnectionState( flow ) );
        set( "flow", flow.getId() );
    }

    @Override
    public String getName() {
        return "remove info need";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            describeTarget( flow );
            Change change = new Change( Type.Removed, flow );
            commander.getPlanDao().disconnect( flow );
            releaseAnyLockOn( commander, flow );
            return change;
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
        AddNeed command = new AddNeed( getUserName() );
        command.setArguments( getArguments() );
        return command;
    }
}
