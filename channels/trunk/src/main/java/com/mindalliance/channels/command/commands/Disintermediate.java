package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Remove a part from flows where the part acts as an intermediate.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 21, 2009
 * Time: 4:06:09 PM
 */
public class Disintermediate extends AbstractCommand {

    public Disintermediate() {
    }

    public Disintermediate( Part part ) {
        // May overshoot if needs locks for unaffected flows.
        needLocksOn( ChannelsUtils.getLockingSetFor( part ) );
        set( "part", part.getId() );
        set( "segment", part.getSegment().getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Disintermediate";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Commander commander ) {
        return super.canDo( commander ) && isIntermediate( commander );
    }

    private boolean isIntermediate( Commander commander ) {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Part part = (Part) segment.getNode( (Long) get( "part" ) );
            return !findIntermediations( part ).isEmpty();
        } catch ( CommandException e ) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Part part = (Part) segment.getNode( (Long) get( "part" ) );
        describeTarget( part );                
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( part );
            set( "subCommands", multi );
        }
        // else this is a replay
        multi.execute( commander );
        ignoreLock( part.getId() );
        return new Change( Change.Type.Recomposed, segment );
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
        MultiCommand multi = new MultiCommand( "add intermediation" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

    // Create a capability and/or need if not repetitive
    private MultiCommand makeSubCommands( Part part ) {
        MultiCommand subCommands = new MultiCommand( "disintermediate - extra" );
        subCommands.setMemorable( false );
        List<Flow[]> inOuts = findIntermediations( part );
        // To avoid disconnecting a flow more than once
        Set<Flow> receivesToDisconnect = new HashSet<Flow>();
        Set<Flow> sendsToDisconnect = new HashSet<Flow>();
        for ( Flow[] inOut : inOuts ) {
            Flow receive = inOut[0];
            Flow send = inOut[1];
            // create flow bypassing intermediate part
            Command directConnect = new ConnectWithFlow(
                    receive.getSource(),
                    send.getTarget(),
                    receive.getName() );
            // Use eois of receive and other attributes of send
            Map<String, Object> attributes = ChannelsUtils.getFlowAttributes( send );
            attributes.put( "eois", send.copyEois() );
            directConnect.set( "attributes", attributes );
            subCommands.addCommand( directConnect );
            sendsToDisconnect.add( send );
            receivesToDisconnect.add( receive );
        }
        for ( Flow receive : receivesToDisconnect ) {
            if ( !hasNeed( part, receive.getName() ) ) {
                AddNeed addNeed = new AddNeed();
                addNeed.set( "segment", get( "segment" ) );
                addNeed.set( "part", get( "part" ) );
                addNeed.set( "name", receive.getName() );
                addNeed.set( "attributes", ChannelsUtils.getFlowAttributes( receive ) );
                subCommands.addCommand( addNeed );
                subCommands.addCommand( new DisconnectFlow( receive ) );
            }
        }
        for ( Flow send : sendsToDisconnect ) {
            if ( !hasCapability( part, send.getName() ) ) {
                AddCapability addCapability = new AddCapability();
                addCapability.set( "segment", get( "segment" ) );
                addCapability.set( "part", get( "part" ) );
                addCapability.set( "name", send.getName() );
                addCapability.set( "attributes", ChannelsUtils.getFlowAttributes( send ) );
                subCommands.addCommand( addCapability );
                subCommands.addCommand( new DisconnectFlow( send ) );
            }
        }
        return subCommands;
    }

    private boolean hasNeed( Part part, final String name ) {
        return CollectionUtils.exists(
                IteratorUtils.toList( part.receives() ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Flow flow = (Flow) object;
                        return flow.isNeed() && flow.getName().equals( name );
                    }
                }
        );

    }

    private boolean hasCapability( Part part, final String name ) {
        return CollectionUtils.exists(
                IteratorUtils.toList( part.sends() ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Flow flow = (Flow) object;
                        return flow.isCapability() && flow.getName().equals( name );
                    }
                }
        );

    }

    // Find pairs of synonymous internal receives and send on a part.
    private List<Flow[]> findIntermediations( Part part ) {
        List<Flow[]> interms = new ArrayList<Flow[]>();
        Iterator<Flow> receives = part.receives();
        while ( receives.hasNext() ) {
            Flow receive = receives.next();
            if ( receive.isInternal() && receive.getSource().isPart() ) {
                Iterator<Flow> sends = part.sends();
                while ( sends.hasNext() ) {
                    Flow send = sends.next();
                    if ( send.isInternal()
                            && send.getTarget().isPart()
                            && receive.getName().equals( send.getName() ) ) {
                        Flow[] interm = {receive, send};
                        interms.add( interm );
                    }
                }
            }
        }
        return interms;
    }

}

