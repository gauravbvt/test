package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Remove a aprt from flows where the part acts as an intermediate.
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
        addConflicting( part );
        set( "part", part.getId() );
        set( "scenario", part.getScenario().getId() );
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
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Part part = (Part) scenario.getNode( (Long) get( "part" ) );
            Collection receiveNames = CollectionUtils.collect(
                    IteratorUtils.filteredIterator( part.requirements(), new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return ((Flow)obj).isInternal();
                        }
                    }),
                    new Transformer() {
                        public Object transform( Object obj ) {
                            return ( (Flow) obj ).getName();
                        }
                    } );
            Collection sendNames = CollectionUtils.collect(
                    IteratorUtils.filteredIterator( part.outcomes(), new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return ((Flow)obj).isInternal();
                        }
                    }),
                    new Transformer() {
                        public Object transform( Object obj ) {
                            return ( (Flow) obj ).getName();
                        }
                    } );
            return !CollectionUtils.intersection( receiveNames, sendNames ).isEmpty();
        } catch ( CommandException e ) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
        Part part = (Part) scenario.getNode( (Long) get( "part" ) );
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( part );
            set( "subCommands", multi );
        }
        // else this is a replay
        multi.execute( commander );
        ignoreLock( part.getId() );
        return new Change( Change.Type.Recomposed, scenario );
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
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "add intermediation" );
        multi.setUndoes( getName() );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.makeUndoCommand( commander ) );
        return multi;
    }

    // Create a capability and/or need if not repetitive
    private MultiCommand makeSubCommands( Part part ) {
        MultiCommand subCommands = new MultiCommand( "disintermediate - extra" );
        subCommands.setMemorable( false );
        List<Flow[]> inOuts = findIntermediations( part );
        // To avoid disconnecting a flow more than once
        Set<Flow> flowsToDisconnect = new HashSet<Flow>();
        for ( Flow[] inOut : inOuts ) {
            Flow receive = inOut[0];
            Flow send = inOut[1];
            // create flow bypassing intermediate part
            Command directConnect = new ConnectWithFlow(
                    receive.getSource(),
                    send.getTarget(),
                    receive.getName() );
            // Use description of receive and other attributes of send
            Map<String, Object> attributes = CommandUtils.getFlowAttributes( send );
            attributes.put( "description", send.getDescription() );
            directConnect.set( "attributes", attributes );
            subCommands.addCommand( directConnect );
            flowsToDisconnect.add( send );
            flowsToDisconnect.add( receive );
        }
        for ( Flow flow : flowsToDisconnect ) {
            subCommands.addCommand( new DisconnectFlow( flow ));
        }
        return subCommands;
    }

    // Find pairs of synonymous internal receives and send on a part.
    private List<Flow[]> findIntermediations( Part part ) {
        List<Flow[]> interms = new ArrayList<Flow[]>();
        Iterator<Flow> receives = part.requirements();
        while ( receives.hasNext() ) {
            Flow receive = receives.next();
            if ( receive.isInternal() ) {
                Iterator<Flow> sends = part.outcomes();
                while ( sends.hasNext() ) {
                    Flow send = sends.next();
                    if ( send.isInternal() && receive.getName().equals( send.getName() ) ) {
                        Flow[] interm = {receive, send};
                        interms.add( interm );
                    }
                }
            }
        }
        return interms;
    }

}
