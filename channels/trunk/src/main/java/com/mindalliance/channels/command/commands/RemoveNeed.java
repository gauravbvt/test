package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.util.ChannelsUtils;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 6, 2009
 * Time: 9:04:00 AM
 */
public class RemoveNeed extends AbstractCommand {

    public RemoveNeed() {
    }

    public RemoveNeed( Flow flow ) {
        needLocksOn( ChannelsUtils.getLockingSetFor( flow ) );
        setArguments( ChannelsUtils.getFlowState( flow ) );
        set( "flow", flow.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "remove info need";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            flow.disconnect( commander.getPlanDao() );
            commander.releaseAnyLockOn( flow );
            return new Change( Change.Type.Removed, flow );
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
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        AddNeed command = new AddNeed();
        command.setArguments( getArguments() );
        return command;
    }
}
