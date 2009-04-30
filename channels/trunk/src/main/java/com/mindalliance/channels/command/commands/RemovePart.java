package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.MultiCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Command to remove a part from a scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 5, 2009
 * Time: 1:40:31 PM
 */
public class RemovePart extends AbstractCommand {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( RemovePart.class );


    public RemovePart() {
    }

    public RemovePart( final Part part ) {
        super();
        addConflicting( part );
        needLocksOn( CommandUtils.getLockingSetFor( part ) );
        set( "part", part.getId() );
        set( "scenario", part.getScenario().getId() );
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
    public Change execute( Commander commander ) throws CommandException {
        DataQueryObject dqo = commander.getDqo();
        Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
        Part part = (Part) scenario.getNode( commander.resolveId( (Long) get( "part" ) ) );
        // Double check in case this is an undo-undo
        if ( !commander.canDo( this ) )
            throw new CommandException( "Someone is making changes." );
        set( "part", part.getId() );
        set( "partState", CommandUtils.getPartState( part ) );
        if ( scenario.countParts() == 1 ) {
            Part defaultPart = dqo.createPart( scenario );
            set( "defaultPart", defaultPart.getId() );
        }
        removePart( part, dqo );
        commander.releaseAnyLockOn( part );
        ignoreLock( commander.resolveId( (Long) get( "part" ) ) );
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
    @SuppressWarnings( "unchecked" )
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "add part" );
        multi.setUndoes( getName() );
        // Reconstitute part
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            AddPart addPart = new AddPart( scenario );
            Map<String, Object> partState = (Map<String, Object>) get( "partState" );
            addPart.set( "part", commander.resolveId( (Long) get( "part" ) ) );
            if ( get( "defaultPart" ) != null ) {
                addPart.set( "defaultPart", get( "defaultPart" ) );
            }
            addPart.set( "partState", partState );
            multi.addCommand( addPart );
            // Disconnect any added needs and capabilities - don't fail if not found
            List<Long> addedNeeds = (List<Long>) get( "addedNeeds" );
            if ( addedNeeds != null ) {
                for ( long id : addedNeeds ) {
                    // It may not have been put in snapshot yet.
                    Long flowId = commander.resolveId( id );
                    if ( flowId != null )  {
                        Flow flow = scenario.findFlow( flowId );
                        multi.addCommand( new RemoveNeed( flow ) );
                    }
                    else {
                        LOG.info( "Info need not found at " + id );
                    }
                }
            }
            List<Long> addedCapabilities = (List<Long>) get( "addedCapabilities" );
            if ( addedCapabilities != null ) {
                for ( long id : addedCapabilities ) {
                    // It may not have been put in snapshot yet.
                    Long flowId = commander.resolveId( id );
                    if ( flowId != null )  {
                        Flow flow = scenario.findFlow( flowId );
                        multi.addCommand( new RemoveCapability( flow ) );
                    }
                    else {
                        LOG.info( "Info capability not found at " + id );
                    }
                }
            }
            // Recreate disconnected flows, possibly external, where possible
            List<Map<String, Object>> removed = (List<Map<String, Object>>) get( "removedFlows" );
            for ( Map<String, Object> identity : removed ) {
/*
                Command command;
                Long removedFlowId = commander.resolveId( (Long) identity.get( "flow " ) );
                Map<String, Object> state = (Map<String, Object>) identity.get( "state" );
                Long otherId = (Long) state.get( "other" );
                Scenario otherScenario = commander.resolve(
                        Scenario.class,
                        (Long) state.get( "otherScenario" ) );
                Node other = CommandUtils.resolveNode( otherId, otherScenario, dqo );
                if ( otherId != null ) {
                    // other is a part (part to part flow)
                    command = new ConnectWithFlow();
                } else {
                    // other node is a connector
                    boolean isOutcome = (Boolean) state.get( "isOutcome" );
                    if ( isOutcome ) {
                        command = new AddCapability();
                    } else {
                        command = new AddNeed();
                    }
                }
                command.setArguments( state );
                command.set( "flow", removedFlowId );
                multi.addCommand( command );
*/
                Command connectWithFlow = new ConnectWithFlow();
                connectWithFlow.setArguments( (Map<String, Object>) identity.get( "state" ) );
                // Id of deleted flow to be recreated -- passed along for mapping old to new
                connectWithFlow.set( "flow", identity.get( "flow" ) );
                multi.addCommand( connectWithFlow );
                // Missing arguments scenario and part.
                // Use the result of command addPart to supply missing arguments to connectWithFlow
                multi.addLink( addPart, "id", connectWithFlow, "part" );
                multi.addLink( addPart, "scenario.id", connectWithFlow, "scenario" );
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
     * @param dqo a data query object
     */
    private void removePart( Part part, DataQueryObject dqo ) {
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
            Map<String, Object> flowSnapshot = new HashMap<String, Object>();
            flowSnapshot.put( "flow", in.getId() );
            flowSnapshot.put( "state", flowState );
            removedFlows.add( flowSnapshot );
            // If the node to be removed is a part,
            // preserve the outcome of the source the flow represents
            if ( in.isInternal()
                    && in.getSource().isPart()
                    && !in.getSource().hasMultipleOutcomes( in.getName() ) ) {
                Flow flow = dqo.connect(
                        in.getSource(),
                        dqo.createConnector( scenario ), in.getName() );
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
            Map<String, Object> flowSnapshot = new HashMap<String, Object>();
            flowSnapshot.put( "flow", out.getId() );
            flowSnapshot.put( "state", flowState );
            removedFlows.add( flowSnapshot );
            // If the node to be removed is a part,
            // preserve the outcome of the source the flow represents
            if ( out.isInternal()
                    && out.getTarget().isPart()
                    && !out.getSource().hasMultipleRequirements( out.getName() ) ) {
                Flow flow = dqo.connect(
                        dqo.createConnector( scenario ),
                        out.getTarget(), out.getName() );
                flow.initFrom( out );
                addedNeeds.add( flow.getId() );
            }
        }
        // Disconnects all requirements and outcomes of the part,
        // and removes the part from the scenario.
        scenario.removeNode( part );
        set( "addedNeeds", addedNeeds );
        set( "addedCapabilities", addedCapabilities );
        set( "removedFlows", removedFlows );
    }

}
