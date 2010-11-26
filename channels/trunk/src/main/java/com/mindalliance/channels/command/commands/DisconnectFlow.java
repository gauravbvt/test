package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.util.ChannelsUtils;

import java.util.Map;

/**
 * Disconnect a flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 10:00:00 PM
 */
public class DisconnectFlow extends AbstractCommand {

    public DisconnectFlow() {
    }

    public DisconnectFlow( Flow flow ) {
        assert !flow.isNeed() && !flow.isCapability();
        needLocksOn( ChannelsUtils.getLockingSetFor( flow ) );
        set( "segment", flow.getSegment().getId() );
        set( "flow", flow.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "remove flow";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            assert get( "flow" ) != null;
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            set( "flowState", ChannelsUtils.getFlowState( flow ) );
            describeTarget( flow );                    
            commander.getPlanDao().disconnect( flow );
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
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        Command connectWithFlow = new ConnectWithFlow();
        connectWithFlow.setArguments( (Map<String, Object>) get( "flowState" ) );
        // The previous id of the flow being re-connected
        connectWithFlow.set( "flow", get( "flow" ) );
        return connectWithFlow;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Flow flow;
        try {
            flow = segment.findFlow( (Long) get( "flow" ) );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh" );
        }
        if ( flow.isCapability() ) return "Remove capability";
        else if ( flow.isNeed() ) return "Remove need";
        else return "Remove flow";
    }

}
