package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;

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
public class SatisfyNeed extends AbstractCommand {

    public SatisfyNeed() {
    }

    public SatisfyNeed( Flow need, Flow capability, Scenario context ) {
        addConflicting( need );
        addConflicting( capability );
        needLocksOn( CommandUtils.getLockingSetFor( need ) );
        needLocksOn( CommandUtils.getLockingSetFor( capability ) );
        Map<String, Object> args = new HashMap<String, Object>();
        args.put( "context", context.getId() );
        args.put( "needScenario", need.getScenario().getId() );
        args.put( "need", need.getId() );
        args.put( "capabilityScenario", capability.getScenario().getId() );
        args.put( "capability", capability.getId() );
        setArguments( args );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "satisfy need";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Flow newFlow;
        DataQueryObject dqo = commander.getDqo();
        try {
            Scenario context = commander.resolve( Scenario.class, (Long) get( "context" ) );
            Scenario needScenario = commander.resolve( Scenario.class, (Long) get( "needScenario" ) );
            Flow need = needScenario.findFlow( commander.resolveId( (Long) get( "need" ) ) );
            Scenario capabilityScenario = commander.resolve( Scenario.class, (Long) get( "capabilityScenario" ) );
            Flow capability = capabilityScenario.findFlow(
                    commander.resolveId( (Long) get( "capability" ) ) );
            Node fromNode;
            Node toNode;
            if ( needScenario == capabilityScenario ) {
                // Internal - from capability's part to need's part
                fromNode = capability.getSource();
                toNode = need.getTarget();
            } else {
                // External - which connector to use depends on context
                if ( context == capabilityScenario ) {
                    // from capability's part to need's connector
                    fromNode = capability.getSource();
                    toNode = need.getSource();
                } else {
                    fromNode = capability.getTarget();
                    toNode = need.getTarget();
                }
            }
            newFlow = dqo.connect( fromNode, toNode, need.getName() );
            newFlow.setSignificanceToSource( capability.getSignificanceToSource() );
            newFlow.setSignificanceToTarget( need.getSignificanceToTarget() );
            newFlow.setChannels( need.isAskedFor() ? capability.getChannels() : need.getChannels() );
            newFlow.setMaxDelay( need.getMaxDelay() );
            commander.mapId( (Long) get( "newFlow" ), newFlow.getId() );
            set( "satisfy", newFlow.getId() );
            set( "need", need.getId() );
            set( "needState", CommandUtils.getFlowState( need ) );
            set("capability", capability.getId() );
            set( "capabilityState", CommandUtils.getFlowState( capability ) );
            need.disconnect();
            capability.disconnect();
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
            // Recreate need given state and old id
            Command connectNeed = new ConnectWithFlow();
            connectNeed.setArguments( (Map<String, Object>) get( "needState" ) );
            connectNeed.set( "flow", get( "need" ) );
            multi.addCommand( connectNeed );
            // Recreate capability given state and old id
            Command connectCapability = new ConnectWithFlow();
            connectCapability.setArguments( (Map<String, Object>) get( "capabilityState" ) );
            connectCapability.set( "flow", get( "capability" ) );
            multi.addCommand( connectCapability );
            // Disconnect need satisfying flow
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "context" ) );
            Flow newFlow = scenario.findFlow( commander.resolveId( (Long) get( "satisfy" ) ) );
            Command disconnectFlow = new DisconnectFlow( newFlow );
            multi.addCommand( disconnectFlow );
            return multi;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

}
