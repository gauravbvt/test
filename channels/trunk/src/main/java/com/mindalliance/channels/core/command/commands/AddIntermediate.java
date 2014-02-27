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
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Insert an intermediate part in a flow.
 */
public class AddIntermediate extends AbstractCommand {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AddIntermediate.class );


    public AddIntermediate() {
        this( "daemon" );
    }

    public AddIntermediate( String userName ) {
        super( userName );
    }

    public AddIntermediate( String userName, Flow flow ) {
        this( userName );
        needLocksOn( ChannelsUtils.getLockingSetFor( flow ) );
        set( "flow", flow.getId() );
        set( "segment", flow.getSegment().getId() );
    }

    @Override
    public String getName() {
        return "intermediate";
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
                && segment.isModifiabledBy( getUserName(), commander.getCommunityService() )
                && canIntermediate( commander ) ;
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


    @Override
    public Change execute( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            describeTarget( flow );
            MultiCommand multi = (MultiCommand) get( "subCommands" );
            if ( multi == null ) {
                multi = makeSubCommands( segment, flow, commander );
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

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( getUserName(), "disintermediate" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

    // Create a capability and/or need if not repetitive
    private MultiCommand makeSubCommands( Segment segment, Flow flow, Commander commander ) {
        MultiCommand subCommands = new MultiCommand( getUserName(), "breakup flow - extra" );
        subCommands.setMemorable( false );
        // make intermediate part
        Command addPart = new AddPart( getUserName(), segment );
        subCommands.addCommand( addPart );
        // connect to intermediate
        Command toIntermediate = new ConnectWithFlow( getUserName() );
        toIntermediate.set( "isSend", true );
        toIntermediate.set( "part", flow.getSource().getId() );
        toIntermediate.set( "segment", segment.getId() );
        toIntermediate.set( "otherSegment", segment.getId() );
        toIntermediate.set( "name", flow.getName() );
        subCommands.addCommand( toIntermediate );
        // The intermediate is the target of the new flow
        subCommands.addLink( addPart, "id", toIntermediate, "other" );
        // connect intermediate to new flow's target
        Command toTarget = new ConnectWithFlow( getUserName() );
        toTarget.set( "isSend", true );
        toTarget.set( "segment", segment.getId() );
        toTarget.set( "otherSegment", segment.getId() );
        toTarget.set( "other", flow.getTarget().getId() );
        toTarget.set( "name", flow.getName() );
        toTarget.set( "attributes", flow.mapState() );
        subCommands.addCommand( toTarget );
        // connect intermediate to new flow's source
        subCommands.addLink( addPart, "id", toTarget, "part" );
        // remove the flow
        subCommands.addCommand( commander.makeRemoveFlowCommand( getUserName(), flow ));
        return subCommands;
    }

}
