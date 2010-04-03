package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.util.ChannelsUtils;

/**
 * Insert an intermediate part in a flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 21, 2009
 * Time: 2:15:18 PM
 */
public class AddIntermediate extends AbstractCommand {

    public AddIntermediate() {
    }

    public AddIntermediate( Flow flow ) {
        needLocksOn( ChannelsUtils.getLockingSetFor( flow ) );
        set( "flow", flow.getId() );
        set( "segment", flow.getSegment().getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "add intermediate";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Commander commander ) {
        return super.canDo( commander ) && canIntermediate( commander );
    }

    private boolean canIntermediate( Commander commander ) {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            return flow.isInternal()
                    && !flow.getName().isEmpty()
                    && flow.getSource().isPart()
                    && flow.getTarget().isPart();
        } catch ( CommandException e ) {
            return false;
        } catch ( NotFoundException e ) {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            MultiCommand multi = (MultiCommand) get( "subCommands" );
            if ( multi == null ) {
                multi = makeSubCommands( segment, flow );
                set( "subCommands", multi );
            }
            // else this is a replay
            multi.execute( commander );
            ignoreLock( flow.getId() );
            return new Change( Change.Type.Recomposed, segment );
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
        MultiCommand multi = new MultiCommand( "disintermediate" );
        multi.setUndoes( getName() );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

    // Create a capability and/or need if not repetitive
    private MultiCommand makeSubCommands( Segment segment, Flow flow ) {
        MultiCommand subCommands = new MultiCommand( "breakup flow - extra" );
        subCommands.setMemorable( false );
        // make intermediate part
        Command addPart = new AddPart( segment );
        subCommands.addCommand( addPart );
        // connect to intermediate
        Command toIntermediate = new ConnectWithFlow();
        toIntermediate.set( "isSend", true );
        toIntermediate.set( "part", flow.getSource().getId() );
        toIntermediate.set( "segment", segment.getId() );
        toIntermediate.set( "otherSegment", segment.getId() );
        toIntermediate.set( "name", flow.getName() );
        subCommands.addCommand( toIntermediate );
        // The intermediate is the target of the new flow
        subCommands.addLink( addPart, "id", toIntermediate, "other" );
        // connect intermediate to new flow's target
        Command toTarget = new ConnectWithFlow();
        toTarget.set( "isSend", true );
        toTarget.set( "segment", segment.getId() );
        toTarget.set( "otherSegment", segment.getId() );
        toTarget.set( "other", flow.getTarget().getId() );
        toTarget.set( "name", flow.getName() );
        toTarget.set( "attributes", ChannelsUtils.getFlowAttributes( flow ) );
        subCommands.addCommand( toTarget );
        // connect intermediate to new flow's source
        subCommands.addLink( addPart, "id", toTarget, "part" );
        // remove the flow
        subCommands.addCommand( new DisconnectFlow( flow ));
        return subCommands;
    }

}
