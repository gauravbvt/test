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
import com.mindalliance.channels.core.command.commands.UpdateObject.Action;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Moving a part and its flows to another segment.
 */
public class MovePart extends AbstractCommand {

    public MovePart() {
        super( "daemon" );
    }

    public MovePart( String userName, Part part, Segment toSegment ) {
        super( userName );
        set( "part", part.getId() );
        set( "toSegment", toSegment.getId() );
        set( "fromSegment", part.getSegment().getId() );
        needLocksOn( ChannelsUtils.getLockingSetFor( part ) );
        addConflicting( toSegment );
        addConflicting( part.getSegment() );
    }

    @Override
    public String getName() {
        return "move task";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        Segment fromSegment = commander.resolve( Segment.class, (Long) get( "fromSegment" ) );
        Part part = (Part) fromSegment.getNode( (Long) get( "part" ) );
        describeTarget( part );
        Segment toSegment = commander.resolve( Segment.class, (Long) get( "toSegment" ) );
        assert !fromSegment.equals( toSegment );
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( part, toSegment, commander );
            set( "subCommands", multi );
        }
        // else this is a replay
        multi.execute( commander );
        ignoreLocksOn( ChannelsUtils.getLockingSetFor( part ) );
        return new Change( Change.Type.Recomposed, fromSegment );
    }

    private MultiCommand makeSubCommands( Part part, Segment toSegment, Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( getUserName(), "move task - extra" );
        Map<String, Object> partState = part.mapState();
        // add identical part to other segment
        addGoalsToSegment( partState, toSegment, multi, commander );
        Command addPart = new AddPart( getUserName(), toSegment );
        addPart.set( "partState", partState );
        multi.addCommand( addPart );
        // add identical flows to moved part from saved states
        addSends( part, toSegment, multi, addPart );
        addReceives( part, toSegment, multi, addPart );
        // disconnect flows to "old" part
        for ( Flow sharingSend : part.getAllSharingSends() )
            multi.addCommand( commander.makeRemoveFlowCommand( getUserName(), sharingSend ) );

        for ( Flow capability : part.getCapabilities() )
            multi.addCommand( new RemoveCapability( getUserName(), capability ) );

        Iterator<Flow> receives = part.receives();
        while ( receives.hasNext() ) {
            Flow receive = receives.next();
            multi.addCommand( commander.makeRemoveFlowCommand( getUserName(), receive ) );
        }
        // remove "old" part
        multi.addCommand( new RemovePart( getUserName(), part ) );
        return multi;
    }

    @SuppressWarnings( "unchecked" )
    private void addGoalsToSegment( Map<String, Object> partState, Segment toSegment, MultiCommand multi,
                                    Commander commander ) {
        for ( Map<String, Object> goalState : (List<Map<String, Object>>) partState.get( "goals" ) ) {
            Goal goal = commander.getQueryService().goalFromMap( goalState );
            if ( !toSegment.getGoals().contains( goal ) )
                multi.addCommand( new UpdateModelObject( getUserName(), toSegment, "goals", goal, Action.AddUnique ) );
        }
    }

    @SuppressWarnings( "unchecked" )
    private void addSends( Part partToMove, Segment toSegment, MultiCommand multi, Command addPart ) {
        Map<String, Command> addCapabilityCommands = new HashMap<String, Command>();
        Iterator<Flow> capabilities = IteratorUtils.filteredIterator( partToMove.sends(), new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ( (Flow) object ).isCapability();
            }
        } );
        while ( capabilities.hasNext() ) {
            Flow capability = capabilities.next();
            Command addCapability = new AddCapability( getUserName() );
            addCapability.set( "name", capability.getName() );
            addCapability.set( "segment", toSegment.getId() );
            addCapability.set( "attributes", capability.mapState() );
            multi.addLink( addPart, "id", addCapability, "part" );
            multi.addCommand( addCapability );
            addCapabilityCommands.put( capability.getName(), addCapability );
        }
        for ( Flow send : partToMove.getAllSharingSends() ) {
            Map<String, Object> attributes = send.mapState();
            String name = send.getName();
            Command connect = new ConnectWithFlow( getUserName() );
            connect.set( "name", name );
            connect.set( "attributes", attributes );
            if ( !send.getTarget().getSegment().equals( toSegment ) ) {
                // flow will be external to moved part
                // don't create redundant capability
                Command addCapability = addCapabilityCommands.get( name );
                if ( addCapability == null ) {
                    addCapability = new AddCapability( getUserName() );
                    multi.addCommand( addCapability );
                    addCapability.set( "name", name );
                    addCapability.set( "segment", toSegment.getId() );
                    addCapability.set( "attributes", attributes );
                    multi.addLink( addPart, "id", addCapability, "part" );
                    addCapabilityCommands.put( name, addCapability );
                }
                connect.set( "part", send.getTarget().getId() );
                connect.set( "segment", send.getTarget().getSegment().getId() );
                multi.addLink( addCapability, "target.id", connect, "other" );
                connect.set( "isSend", false );  // part is the receiver
                connect.set( "otherSegment", toSegment.getId() );
            } else {
                // flow will be internal to moved part
                multi.addLink( addPart, "id", connect, "part" );
                connect.set( "segment", toSegment.getId() );
                connect.set( "isSend", true );
                connect.set( "other", send.getTarget().getId() ); // target = external part
                connect.set( "otherSegment", toSegment.getId() );
            }
            multi.addCommand( connect );
        }
    }

    private void addReceives( Part partToMove, Segment toSegment, MultiCommand multi, Command addPart ) {
        Segment fromSegment = partToMove.getSegment();
        Iterator<Flow> receives = partToMove.receives();
        while ( receives.hasNext() ) {
            Flow receive = receives.next();
            Map<String, Object> attributes = receive.mapState();
            String name = receive.getName();
            if ( receive.isNeed() ) {
                Command addNeed = new AddNeed( getUserName() );
                addNeed.set( "name", name );
                addNeed.set( "segment", toSegment.getId() );
                addNeed.set( "attributes", attributes );
                multi.addLink( addPart, "id", addNeed, "part" );
                multi.addCommand( addNeed );
            } else {
                // a sharing receive
                Command connect = new ConnectWithFlow( getUserName() );
                connect.set( "name", name );
                connect.set( "attributes", attributes );
                connect.set( "segment", toSegment.getId() );
                connect.set( "isSend", false );
                multi.addLink( addPart, "id", connect, "part" );
                if ( receive.isInternal() ) {
                    // was internal to old part,  flow will be external to moved part
                    Part source = (Part) receive.getSource();
                    Flow capability = source.findCapability( name );
                    if ( capability == null ) {
                        Command addCapability = new AddCapability( getUserName() );
                        multi.addCommand( addCapability );
                        addCapability.set( "name", name );
                        addCapability.set( "segment", fromSegment.getId() );
                        addCapability.set( "attributes", attributes );
                        addCapability.set( "part", source.getId() );
                        multi.addLink( addCapability, "target.id", connect, "other" );
                    } else {
                        connect.set( "other", capability.getTarget().getId() );
                    }
                    connect.set( "otherSegment", fromSegment.getId() );
                } else {
                    if ( receive.getSource().getSegment().equals( toSegment ) ) {
                        // external to internal
                        connect.set( "other", receive.getSource().getId() ); // other = the external part
                        connect.set( "otherSegment", toSegment.getId() );
                    } else {
                        // external stays external
                        Map<String, Object> state = ChannelsUtils.getFlowConnectionState( receive, partToMove );
                        connect.set( "otherSegment", state.get( "otherSegment" ) );
                        connect.set( "other", state.get( "other" ) );
                    }
                }
                multi.addCommand( connect );
            }
        }
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( getUserName(), "unmove task" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }
}
