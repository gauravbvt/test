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
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Command to break up a given flow.
 */
public class BreakUpFlow extends AbstractCommand {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( BreakUpFlow.class );


    public BreakUpFlow() {
        super( "daemon" );
    }

    public BreakUpFlow( String userName, Flow flow ) {
        super( userName );
        needLocksOn( ChannelsUtils.getLockingSetFor( flow ) );
        set( "flow", flow.getId() );
        set( "segment", flow.getSegment().getId() );
    }

    @Override
    public String getName() {
        return "break up flow";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            MultiCommand multi = (MultiCommand) get( "subCommands" );
            if ( multi == null ) {
                multi = makeSubCommands( flow );
                set( "subCommands", multi );
            }
            // else this is a replay
            multi.execute( commander );
            set( "flowState", ChannelsUtils.getFlowConnectionState( flow ) );
            describeTarget( flow );
            commander.getPlanDao().disconnect( flow );
            //            breakup( flow, commander );
            ignoreLock( (Long) get( "flow" ) );
            return new Change( Change.Type.Recomposed, segment );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    @Override
    public boolean canDo( Commander commander ) {
        Segment segment = null;
        try {
            segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        } catch( CommandException e ) {
            LOG.warn( "Segment not found", e );
            return false;
        }
        return super.canDo( commander )
                && segment.isModifiabledBy( getUserName(), commander.getCommunityService() );
    }


    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( getUserName(), "reconnect flow" );
        ConnectWithFlow connectWithFlow = new ConnectWithFlow( getUserName() );
        connectWithFlow.setArguments( (Map<String, Object>) get( "flowState" ) );
        connectWithFlow.set( "flow", get( "flow" ) );
        multi.addCommand( connectWithFlow );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

    // Create a capability and/or need if not repetitive
    private MultiCommand makeSubCommands( Flow flow ) {
        MultiCommand subCommands = new MultiCommand( getUserName(), "breakup flow - extra" );
        subCommands.setMemorable( false );
        if ( flow.isInternal() ) {
            Node source = flow.getSource();
            Node target = flow.getTarget();
            if ( !source.isConnector() && !target.isConnector() ) {
                if ( !source.hasMultipleSends( flow.getName() ) ) {
                    Command addCapability = new AddCapability( getUserName() );
                    addCapability.set( "segment", source.getSegment().getId() );
                    addCapability.set( "part", source.getId() );
                    addCapability.set( "name", flow.getName() );
                    addCapability.set( "attributes", flow.mapState() );
                    subCommands.addCommand( addCapability );
                }
                if ( !target.hasMultipleReceives( flow.getName() ) ) {
                    Command addNeed = new AddNeed( getUserName() );
                    addNeed.set( "segment", target.getSegment().getId() );
                    addNeed.set( "part", target.getId() );
                    addNeed.set( "name", flow.getName() );
                    addNeed.set( "attributes", flow.mapState() );
                    subCommands.addCommand( addNeed );
                }
            }
        }
        return subCommands;
    }
}
