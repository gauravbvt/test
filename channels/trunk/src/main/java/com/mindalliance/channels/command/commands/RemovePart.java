package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.Flow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Command to remove a part from a scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 5, 2009
 * Time: 1:40:31 PM
 */
public class RemovePart extends AbstractCommand {

    public RemovePart() {
    }

    public RemovePart( final Part part ) {
        super();
        addConflicting( part );
        needLocksOn( CommandUtils.getLockingSetFor( part ) );
        setArguments( new HashMap<String, Object>() {
            {
                put( "part", part.getId() );
                put( "scenario", part.getScenario().getId() );
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "remove part";
    }

    /**
     * {@inheritDoc}
     */
    public Object execute( Commander commander ) throws CommandException {
        boolean removed;
        Service service = commander.getService();
        try {
            Scenario scenario = service.find( Scenario.class, (Long) get( "scenario" ) );
            Part part = (Part) scenario.getNode( (Long) get( "part" ) );
            // Double check in case this is an undo-undo
            if ( !commander.canDo( this ) )
                throw new CommandException( "Someone is making changes." );
            addArgument( "partState", CommandUtils.getPartState( part ) );
            removePart( part, service );
            ignoreLock( (Long) get( "part" ) );
            removed = true;
        } catch ( NotFoundException e ) {
            removed = false;
        }
        return removed;
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
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        Service service = commander.getService();
        MultiCommand multi = new MultiCommand();
        // Reconstitute part
        try {
            Scenario scenario = service.find( Scenario.class, (Long) get( "scenario" ) );
            AddPart addPart = new AddPart( scenario );
            Map<String, Object> partState = (Map<String, Object>) get( "partState" );
            addPart.set( "partState", partState );
            multi.addCommand( addPart );
            // Disconnect any added needs and capabilities
            List<Long> addedNeeds = (List<Long>) get( "addedNeeds" );
            if ( addedNeeds != null ) {
                for ( long id : addedNeeds ) {
                    Flow flow = scenario.findFlow( id );
                    multi.addCommand( new RemoveNeed( flow ) );
                }
            }
            List<Long> addedCapabilities = (List<Long>) get( "addedCapabilities" );
            if ( addedCapabilities != null ) {
                for ( long id : addedCapabilities ) {
                    Flow flow = scenario.findFlow( id );
                    multi.addCommand( new RemoveCapability( flow ) );
                }
            }
            // Recreate disconnected flows where possible
            List<Map<String, Object>> removed = (List<Map<String, Object>>) get( "removedFlows" );
            Command command;
            for ( Map<String, Object> fs : removed ) {
                Long otherId = (Long) fs.get( "other" );
                if ( otherId != null ) {
                    // other is a part (part to part flow)
                    command = new ConnectWithFlow();
                } else {
                    // other node is a connector
                    boolean isOutcome = (Boolean) fs.get( "isOutcome" );
                    if ( isOutcome ) {
                        command = new AddCapability();
                    } else {
                        command = new AddNeed();
                    }
                }
                command.setArguments( fs );
                // missing arguments scenario and part
                multi.addCommand( command );
                // Use the result of command addPart to supply arguments to connectWithFlow
                multi.addLink( addPart, "id", command, "part" );
                multi.addLink( addPart, "scenario.id", command, "scenario" );
            }

            // the undo of this multi is a RemovePart with argument
            // part = id of part created by multi's addPart
            Command undoUndo = new RemovePart();
            multi.addLink( addPart, "id", undoUndo, "part" );
            multi.addLink( addPart, "scenario.id", undoUndo, "scenario" );
            multi.setUndoCommand( undoUndo );
            return multi;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    /**
     * Remove a part after making sure that needs and capabilities are preserved.
     *
     * @param part    a part
     * @param service a service
     */
    private void removePart( Part part, Service service ) {
        List<Long> addedNeeds = new ArrayList<Long>();
        List<Long> addedCapabilities = new ArrayList<Long>();
        List<Map<String, Object>> removedFlows = new ArrayList<Map<String, Object>>();
        Scenario scenario = part.getScenario();
        Iterator<Flow> ins = part.requirements();
        while ( ins.hasNext() ) {
            Flow in = ins.next();
            Map<String, Object> flowState = CommandUtils.getFlowState( in, part );
            flowState.remove( "part" );
            flowState.remove( "scenario" );
            removedFlows.add( flowState );
            // If the node to be removed is a part,
            // preserve the outcome of the source the flow represents
            if ( in.isInternal()
                    && in.getSource().isPart()
                    && !in.getSource().hasMultipleOutcomes( in.getName() ) )
            {
                Flow flow = service.connect(
                        in.getSource(),
                        service.createConnector( scenario ), in.getName() );
                flow.initFrom( in );
                addedCapabilities.add( flow.getId() );
            }
        }
        Iterator<Flow> outs = part.outcomes();
        while ( outs.hasNext() ) {
            Flow out = outs.next();
            Map<String, Object> flowState = CommandUtils.getFlowState( out, part );
            flowState.remove( "part" );
            flowState.remove( "scenario" );
            removedFlows.add( flowState );
            // If the node to be removed is a part,
            // preserve the outcome of the source the flow represents
            if ( out.isInternal()
                    && out.getTarget().isPart()
                    && !out.getSource().hasMultipleRequirements( out.getName() ) )
            {
                Flow flow = service.connect(
                        service.createConnector( scenario ),
                        out.getTarget(), out.getName() );
                flow.initFrom( out );
                addedNeeds.add( flow.getId() );
            }
        }
        // Disconnects all requirements and outcomes of the part,
        // and removes the part from the scenario.
        scenario.removeNode( part );
        addArgument( "addedNeeds", addedNeeds );
        addArgument( "addedCapabilities", addedCapabilities );
        addArgument( "removedFlows", removedFlows );
    }

}
