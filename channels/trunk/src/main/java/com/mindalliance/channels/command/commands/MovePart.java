package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.util.ChannelsUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Moving a part and its flows to another segment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 11, 2010
 * Time: 9:20:57 PM
 */
public class MovePart extends AbstractCommand {

    public MovePart() {
    }

    public MovePart( Part part, Segment toSegment ) {
        set( "part", part.getId() );
        set( "toSegment", toSegment.getId() );
        set( "fromSegment", part.getSegment().getId() );
        needLocksOn( ChannelsUtils.getLockingSetFor( part ) );
    }

    /**
     * {@inheritDoc}
     */

    public String getName() {
        return "move task";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Segment fromSegment = commander.resolve( Segment.class, (Long) get( "fromSegment" ) );
        Part part = (Part) fromSegment.getNode( (Long) get( "part" ) );
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

    private MultiCommand makeSubCommands(
            Part part,
            Segment toSegment,
            Commander commander
    ) throws CommandException {
        MultiCommand multi = new MultiCommand( "move task - extra" );
        Map<String, Object> partState = part.mapState();
        // add identical part to other segment
        addGoalsToSegment( partState, toSegment, multi, commander );
        Command addPart = new AddPart( toSegment );
        addPart.set( "partState", partState );
        multi.addCommand( addPart );
        // add identical flows to moved part from saved states
        addSends( part, toSegment, multi, addPart );
        addReceives( part, toSegment, multi, addPart );
        // disconnect flows to "old" part
        Iterator<Flow> sends = part.sends();
        while ( sends.hasNext() ) {
            Flow send = sends.next();
            multi.addCommand( new DisconnectFlow( send ) );
        }
        Iterator<Flow> receives = part.receives();
        while ( receives.hasNext() ) {
            Flow receive = receives.next();
            multi.addCommand( new DisconnectFlow( receive ) );
        }
        // remove "old" part
        multi.addCommand( new RemovePart( part ) );
        return multi;
    }

    @SuppressWarnings( "unchecked" )
    private void addGoalsToSegment(
            Map<String, Object> partState,
            Segment toSegment,
            MultiCommand multi,
            Commander commander ) {
        for ( Map<String, Object> goalState : (List<Map<String, Object>>) partState.get( "goals" ) ) {
            Goal goal = commander.getQueryService().goalFromMap( goalState );
            if ( !toSegment.getGoals().contains( goal ) ) {
                UpdatePlanObject updateSegment = new UpdatePlanObject(
                        toSegment,
                        "goals",
                        goal,
                        UpdateObject.Action.Add );
                multi.addCommand( updateSegment );
            }
        }
    }


    @SuppressWarnings( "unchecked" )
    private void addSends(
            Part partToMove,
            Segment toSegment,
            MultiCommand multi,
            Command addPart ) throws CommandException {
        Segment fromSegment = partToMove.getSegment();
        Map<String, Command> addCapabilityCommands = new HashMap<String, Command>();
        Iterator<Flow> capabilities = IteratorUtils.filteredIterator(
                partToMove.sends(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (Flow) object ).isCapability();
                    }
                } );
        while ( capabilities.hasNext() ) {
            Flow capability = capabilities.next();
            Command addCapability = new AddCapability();
            addCapability.set( "name", capability.getName() );
            addCapability.set( "segment", toSegment.getId() );
            addCapability.set( "attributes", ChannelsUtils.getFlowAttributes( capability ) );
            multi.addLink( addPart, "id", addCapability, "part" );
            multi.addCommand( addCapability );
            addCapabilityCommands.put( capability.getName(), addCapability );
        }
        for ( Flow send : partToMove.getAllSharingSends() ) {
            Map<String, Object> attributes = ChannelsUtils.getFlowAttributes( send );
            String name = send.getName();
            Command connect = new ConnectWithFlow();
            connect.set( "name", name );
            connect.set( "attributes", attributes );
            if ( !send.getTarget().getSegment().equals( toSegment ) ) {
                // flow will be external to moved part
                // don't create redundant capability
                Command addCapability = addCapabilityCommands.get( name );
                if ( addCapability == null ) {
                    addCapability = new AddCapability();
                    multi.addCommand( addCapability );
                    addCapability.set( "name", name );
                    addCapability.set( "segment", toSegment.getId() );
                    addCapability.set( "attributes", attributes );
                    multi.addLink( addPart, "id", addCapability, "part" );
                    addCapabilityCommands.put( name, addCapability );
                }
                connect.set( "part", send.getTarget().getId() );
                connect.set( "segment", fromSegment.getId() );
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

    private void addReceives(
            Part partToMove,
            Segment toSegment,
            MultiCommand multi,
            Command addPart ) throws CommandException {
        Segment fromSegment = partToMove.getSegment();
        Iterator<Flow> receives = partToMove.receives();
        while ( receives.hasNext() ) {
            Flow receive = receives.next();
            Map<String, Object> attributes = ChannelsUtils.getFlowAttributes( receive );
            String name = receive.getName();
            if ( receive.isNeed() ) {
                Command addNeed = new AddNeed();
                addNeed.set( "name", name );
                addNeed.set( "segment", toSegment.getId() );
                addNeed.set( "attributes", attributes );
                multi.addLink( addPart, "id", addNeed, "part" );
                multi.addCommand( addNeed );
            } else {
                // a sharing receive
                Command connect = new ConnectWithFlow();
                connect.set( "name", name );
                connect.set( "attributes", attributes );
                connect.set( "segment", toSegment.getId() );
                connect.set( "isSend", false );
                multi.addLink( addPart, "id", connect, "part" );
                if ( receive.isInternal() ) {
                    // was internal to old part,  flow will be externalto moved part
                    Part source = (Part) receive.getSource();
                    Flow capability = source.findCapability( name );
                    if ( capability == null ) {
                        Command addCapability = new AddCapability();
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
                        Map<String, Object> state = ChannelsUtils.getFlowState( receive, partToMove );
                        connect.set( "otherSegment", state.get( "otherSegment" ) );
                        connect.set( "other", state.get( "other" ) );
                    }
                }
                multi.addCommand( connect );
            }
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
        MultiCommand multi = new MultiCommand( "unmove task" );
        multi.setUndoes( getName() );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

}
