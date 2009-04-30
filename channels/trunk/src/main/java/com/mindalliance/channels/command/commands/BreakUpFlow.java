package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.command.Change;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Command to break up a given flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 7:21:58 PM
 */
public class BreakUpFlow extends AbstractCommand {

    public BreakUpFlow() {
    }

    public BreakUpFlow( Flow flow ) {
        super();
        addConflicting( flow );
        needLocksOn( CommandUtils.getLockingSetFor( flow ) );
        needLockOn( flow.getScenario() );
        set( "flow", flow.getId() );
        set( "scenario", flow.getScenario().getId() );
        set( "flowState", CommandUtils.getFlowState( flow ) );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Break up flow";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Flow flow = scenario.findFlow( commander.resolveId( (Long) get( "flow" ) ) );
            breakup( flow, commander );
            ignoreLock( commander.resolveId( (Long) get( "flow" ) ) );
            return new Change( Change.Type.Recomposed, scenario );
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
        MultiCommand multi = new MultiCommand( "unbreak flow" );
        multi.setUndoes( getName() );
        ConnectWithFlow connectWithFlow = new ConnectWithFlow();
        connectWithFlow.setArguments( (Map<String, Object>) get( "flowState" ) );
        connectWithFlow.set( "flow", commander.resolveId( (Long) get( "flow" ) ) );
        multi.addCommand( connectWithFlow );
        List<Long> addedFlows = (List<Long>) get( "addedFlows" );
        if ( addedFlows != null ) {
            try {
                Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
                for ( Long id : addedFlows ) {
                    Flow addedFlow = scenario.findFlow( commander.resolveId( id ) );
                    DisconnectFlow disconnectFlow = new DisconnectFlow( addedFlow );
                    multi.addCommand( disconnectFlow );
                }
            } catch ( NotFoundException e ) {
                throw new CommandException( "You need to refresh.", e );
            }
        }
        return multi;
    }

    private void breakup( Flow flow, Commander commander ) {
        DataQueryObject dqo = commander.getDqo();
        if ( flow.isInternal() ) {
            List<Long> addedFlows = new ArrayList<Long>();
            Node source = flow.getSource();
            Node target = flow.getTarget();
            if ( !source.isConnector() && !target.isConnector() ) {
                if ( !source.hasMultipleOutcomes( getName() ) ) {
                    Flow newFlow = dqo.connect( source,
                            dqo.createConnector( source.getScenario() ),
                            getName() );
                    newFlow.initFrom( flow );
                    addedFlows.add( newFlow.getId() );
                }
                if ( !target.hasMultipleRequirements( getName() ) ) {
                    Flow newFlow = dqo.connect( dqo.createConnector( target.getScenario() ),
                            target,
                            getName() );
                    newFlow.initFrom( flow );
                    addedFlows.add( newFlow.getId() );
                }
            }
            set( "addedFlows", addedFlows );
        }
        flow.disconnect();
    }

}
