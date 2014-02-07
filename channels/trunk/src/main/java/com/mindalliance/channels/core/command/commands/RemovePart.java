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
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;

import java.util.Iterator;

/**
 * Command to remove a part from a segment after taking a copy.
 */
public class RemovePart extends AbstractCommand {

    public RemovePart() {
        super( "daemon" );
    }

    public RemovePart( String userName, Part part ) {
        super( userName );
        needLocksOn( ChannelsUtils.getLockingSetFor( part ) );
        addConflicting( part.getSegment() );
        set( "part", part.getId() );
        set( "segment", part.getSegment().getId() );
    }

    @Override
    public String getName() {
        return "remove task";
    }

    @Override
    public boolean canDo( Commander commander ) {
        return super.canDo( commander ) && isNotDefaultPart( commander );
    }

    private boolean isNotDefaultPart( Commander commander ) {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            return segment.countParts() > 1;
        } catch ( CommandException e ) {
            return false;
        }
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Part part = (Part) segment.getNode( (Long) get( "part" ) );
        describeTarget( part );
        set( "partState", part.mapState() );
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( part, commander );
            set( "subCommands", multi );
        }
        // else this is a replay
        multi.execute( commander );
        if ( segment.countParts() == 1 ) {
            Part defaultPart = queryService.createPart( segment );
            set( "defaultPart", defaultPart.getId() );
        }
        commander.getPlanDao().removeNode( part, segment );
        releaseAnyLockOn( commander, part );
        ignoreLock( (Long) get( "part" ) );
        return new Change( Type.Recomposed, segment );
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( getUserName(), "add new task" );
        // Reconstitute part
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        AddPart addPart = new AddPart( getUserName(), segment );
        addPart.set( "part", get( "part" ) );
        if ( get( "defaultPart" ) != null )
            addPart.set( "defaultPart", get( "defaultPart" ) );
        addPart.set( "partState", get( "partState" ) );
        multi.addCommand( addPart );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

    /**
     * Make multi command for adding capabilities and needs in the wake of the part's removal.
     *
     * @param part a part
     * @param commander a commander
     * @return a multi command
     */
    private MultiCommand makeSubCommands( Part part, Commander commander ) {
        MultiCommand subCommands = new MultiCommand( getUserName(), "cut task - extra" );
        subCommands.addCommand( new CopyPart( getUserName(), part ) );
        Iterator<Flow> ins = part.receives();
        while ( ins.hasNext() ) {
            Flow in = ins.next();
            if ( in.isSharing() )
                subCommands.addCommand( commander.makeRemoveFlowCommand( getUserName(), in ) );
            else
                subCommands.addCommand( new RemoveNeed( getUserName(), in ) );
            // If the node to be removed is a part,
            // preserve the send of the source the flow represents
            if ( in.isInternal() && in.getSource().isPart() && !in.getSource().hasMultipleSends( in.getName() ) ) {
                Command addCapability = new AddCapability( getUserName() );
                addCapability.set( "segment", in.getSource().getSegment().getId() );
                addCapability.set( "part", in.getSource().getId() );
                addCapability.set( "name", in.getName() );
                addCapability.set( "attributes", in.mapState() );
                subCommands.addCommand( addCapability );
            }
        }
        Iterator<Flow> outs = part.sends();
        while ( outs.hasNext() ) {
            Flow out = outs.next();
            if ( out.isSharing() )
                subCommands.addCommand( commander.makeRemoveFlowCommand( getUserName(), out ) );
            else
                subCommands.addCommand( new RemoveCapability( getUserName(), out ) );
            // If the node to be removed is a part,
            // preserve the send of the source the flow represents
            if ( out.isInternal() && out.getTarget().isPart()
                 && !out.getSource().hasMultipleReceives( out.getName() ) ) {
                Command addNeed = new AddNeed( getUserName() );
                addNeed.set( "segment", out.getTarget().getSegment().getId() );
                addNeed.set( "part", out.getTarget().getId() );
                addNeed.set( "name", out.getName() );
                addNeed.set( "attributes", out.mapState() );
                subCommands.addCommand( addNeed );
            }
        }
        return subCommands;
    }
}
