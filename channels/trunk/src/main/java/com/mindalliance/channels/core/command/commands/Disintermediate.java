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
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Remove a part from flows where the part acts as an intermediate.
 */
public class Disintermediate extends AbstractCommand {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( Disintermediate.class );


    public Disintermediate() {
        super( "daemon" );
    }

    public Disintermediate( String userName, Part part ) {
        // May overshoot if needs locks for unaffected flows.
        super( userName );
        needLocksOn( ChannelsUtils.getLockingSetFor( part ) );
        set( "part", part.getId() );
        set( "segment", part.getSegment().getId() );
    }

    @Override
    public String getName() {
        return "disintermediate";
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
                && isIntermediate( commander );
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

    @Override
    public Change execute( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Part part = (Part) segment.getNode( (Long) get( "part" ) );
        describeTarget( part );
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( part, commander );
            set( "subCommands", multi );
        }
        // else this is a replay
        multi.execute( commander );
        ignoreLock( part.getId() );
        return new Change( Change.Type.Recomposed, segment );
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( getUserName(), "add intermediation" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

    // Create a capability and/or need if not repetitive
    private MultiCommand makeSubCommands( Part part, Commander commander ) {
        MultiCommand subCommands = new MultiCommand( getUserName(), "disintermediate - extra" );
        subCommands.setMemorable( false );
        // To avoid disconnecting a flow more than once
        Set<Flow> receivesToDisconnect = new HashSet<Flow>();
        Set<Flow> sendsToDisconnect = new HashSet<Flow>();
        for ( Flow[] inOut : findIntermediations( part ) ) {
            Flow receive = inOut[0];
            Flow send = inOut[1];
            // create flow bypassing intermediate part
            Command directConnect =
                    new ConnectWithFlow( getUserName(), receive.getSource(), send.getTarget(), receive.getName() );
            // Use eois of receive and other attributes of send
            Map<String, Object> attributes = send.mapState();
            attributes.put( "eois", send.copyEois() );
            directConnect.set( "attributes", attributes );
            subCommands.addCommand( directConnect );
            sendsToDisconnect.add( send );
            receivesToDisconnect.add( receive );
        }
        for ( Flow receive : receivesToDisconnect )
            if ( !hasNeed( part, receive.getName() ) ) {
                AddNeed addNeed = new AddNeed( getUserName() );
                addNeed.set( "segment", get( "segment" ) );
                addNeed.set( "part", get( "part" ) );
                addNeed.set( "name", receive.getName() );
                addNeed.set( "attributes", receive.mapState() );
                subCommands.addCommand( addNeed );
                subCommands.addCommand( commander.makeRemoveFlowCommand( getUserName(), receive ) );
            }
        for ( Flow send : sendsToDisconnect )
            if ( !hasCapability( part, send.getName() ) ) {
                AddCapability addCapability = new AddCapability( getUserName() );
                addCapability.set( "segment", get( "segment" ) );
                addCapability.set( "part", get( "part" ) );
                addCapability.set( "name", send.getName() );
                addCapability.set( "attributes", send.mapState() );
                subCommands.addCommand( addCapability );
                subCommands.addCommand( commander.makeRemoveFlowCommand( getUserName(), send ) );
            }
        return subCommands;
    }

    private static boolean hasNeed( Part part, final String name ) {
        return CollectionUtils.exists( IteratorUtils.toList( part.receives() ), new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                Flow flow = (Flow) object;
                return flow.isNeed() && flow.getName().equals( name );
            }
        } );
    }

    private static boolean hasCapability( Part part, final String name ) {
        return CollectionUtils.exists( IteratorUtils.toList( part.sends() ), new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                Flow flow = (Flow) object;
                return flow.isCapability() && flow.getName().equals( name );
            }
        } );
    }

    // Find pairs of synonymous internal receives and send on a part.
    private static List<Flow[]> findIntermediations( Part part ) {
        List<Flow[]> interms = new ArrayList<Flow[]>();
        Iterator<Flow> receives = part.receives();
        while ( receives.hasNext() ) {
            Flow receive = receives.next();
            if ( receive.isInternal() && receive.getSource().isPart() ) {
                Iterator<Flow> sends = part.sends();
                while ( sends.hasNext() ) {
                    Flow send = sends.next();
                    if ( send.isInternal() && send.getTarget().isPart()
                         && receive.getName().equals( send.getName() ) )
                    {
                        Flow[] interm = { receive, send };
                        interms.add( interm );
                    }
                }
            }
        }
        return interms;
    }
}

