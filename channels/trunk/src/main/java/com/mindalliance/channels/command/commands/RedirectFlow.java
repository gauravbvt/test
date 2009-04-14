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
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Scenario;

import java.util.HashMap;
import java.util.Map;

/**
 * Set the node at the other side of this flow by connecting "through" a connector
 * to the part in the connector's innerflow.
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
        args.put( "connected", connector.getInnerFlow().getId() );
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
        DataQueryObject dqo = commander.getDqo();
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Flow redirectedFlow = scenario.findFlow( commander.resolveId( (Long) get( "flow" ) ) );
            Scenario otherScenario = commander.resolve( Scenario.class, (Long) get( "otherScenario" ) );
            Flow connectorFlow = otherScenario.findFlow(
                    commander.resolveId( (Long) get( "connected" ) ) );
            boolean isOutcome = (Boolean) get( "outcome" );
            Connector connector = isOutcome
                    ? (Connector)connectorFlow.getSource()
                    : (Connector)connectorFlow.getTarget();
            if ( isOutcome ) {
                if ( connectorFlow.getScenario() != redirectedFlow.getSource().getScenario() ) {
                    newFlow = dqo.connect(
                            redirectedFlow.getSource(),
                            connector,
                            connectorFlow.getName() );
                    newFlow.initFrom( redirectedFlow );
                } else {
                    newFlow = dqo.connect(
                            redirectedFlow.getSource(),
                            connectorFlow.getTarget(),
                            connectorFlow.getName() );
                    newFlow.initFrom( connectorFlow );
                }
            } else {
                if ( connectorFlow.getScenario() != redirectedFlow.getTarget().getScenario() ) {
                    newFlow = dqo.connect(
                            connector,
                            redirectedFlow.getTarget(),
                            connectorFlow.getName() );
                    newFlow.initFrom( redirectedFlow );
                } else {
                    newFlow = dqo.connect(
                            connectorFlow.getSource(),
                            redirectedFlow.getTarget(),
                            connectorFlow.getName() );
                    newFlow.initFrom( connectorFlow );
                }
            }
            commander.mapId( (Long) get( "newFlow" ), newFlow.getId() );
            set( "newFlow", newFlow.getId() );
            set( "oldFlowState", CommandUtils.getFlowState( redirectedFlow ) );
            redirectedFlow.disconnect();
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
            // The flow to be re-connected.
            connectWithFlow.set( "flow", get( "flow" ) );
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
