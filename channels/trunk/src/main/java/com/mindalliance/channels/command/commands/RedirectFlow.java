package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Scenario;

import java.util.HashMap;
import java.util.Map;

/**
 * Redirect a flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 9:15:10 PM
 */
public class RedirectFlow extends AbstractCommand {

    public RedirectFlow() {
    }

    public RedirectFlow( final Flow flow, final Connector connector, final boolean isOutcome ) {
        addConflicting( flow );
        needLockOn( connector );
        needLocksOn( CommandUtils.getLockingSetFor( flow ) );
        Map<String, Object> args = new HashMap<String, Object>();
        args.put( "scenario", flow.getScenario().getId() );
        args.put( "flow", flow.getId() );
        args.put( "otherScenario", connector.getScenario().getId() );
        args.put( "connector", connector.getId() );
        args.put( "outcome", isOutcome );
        setArguments( args );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "redirect flow";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Flow newFlow;
        Service service = commander.getService();
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Flow oldFlow = scenario.findFlow( commander.resolveId( (Long) get( "flow" ) ) );
            Scenario otherScenario = commander.resolve( Scenario.class, (Long) get( "otherScenario" ) );
            Connector connector = (Connector) otherScenario.getNode(
                    commander.resolveId( (Long) get( "connector" ) ) );
            boolean isOutcome = (Boolean) get( "outcome" );
            Flow connectorFlow = connector.getInnerFlow();
            if ( isOutcome ) {
                if ( connector.getScenario() != oldFlow.getSource().getScenario() ) {
                    newFlow = service.connect(
                            oldFlow.getSource(),
                            connector,
                            connectorFlow.getName() );
                    newFlow.initFrom( oldFlow );
                } else {
                    newFlow = service.connect(
                            oldFlow.getSource(),
                            connectorFlow.getTarget(),
                            connectorFlow.getName() );
                    newFlow.initFrom( connectorFlow );
                }
            } else {
                if ( connector.getScenario() != oldFlow.getTarget().getScenario() ) {
                    newFlow = service.connect(
                            connector,
                            oldFlow.getTarget(),
                            connectorFlow.getName() );
                    newFlow.initFrom( oldFlow );
                } else {
                    newFlow = service.connect(
                            connectorFlow.getSource(),
                            oldFlow.getTarget(),
                            connectorFlow.getName() );
                    newFlow.initFrom( connectorFlow );
                }
            }
            commander.mapId( (Long) get( "newFlow" ), newFlow.getId() );
            addArgument( "newFlow", newFlow.getId() );
            addArgument( "oldFlowState", CommandUtils.getFlowState( oldFlow ) );
            oldFlow.disconnect();
            // What about reporting the removal of the disconnected flow?
            return new Change( Change.Type.Added, newFlow );
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
    @SuppressWarnings( "unchecked" )
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( getName() );
        multi.setUndoes( getName() );
        try {
            // Reconnect old flow
            Command connectWithFlow = new ConnectWithFlow();
            connectWithFlow.setArguments( (Map<String, Object>) get( "oldFlowState" ) );
            multi.addCommand( connectWithFlow );
            // Disconnect newFlow
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Flow newFlow = scenario.findFlow( commander.resolveId( (Long) get( "newFlow" ) ) );
            Command disconnectFlow = new DisconnectFlow( newFlow );
            multi.addCommand( disconnectFlow );
            return multi;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

}
