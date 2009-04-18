package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.NotFoundException;

import java.util.Map;

/**
 * DIsconnect a flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 10:00:00 PM
 */
public class DisconnectFlow extends AbstractCommand {

    public DisconnectFlow() {
    }

    public DisconnectFlow( Flow flow ) {
        addConflicting( flow );
        needLocksOn( CommandUtils.getLockingSetFor( flow ) );
        set( "scenario", flow.getScenario().getId() );
        set( "flow", flow.getId() );
        set( "flowState", CommandUtils.getFlowState( flow ) );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "remove flow";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Flow flow = scenario.findFlow( commander.resolveId( (Long) get( "flow" ) ) );
            flow.disconnect();
            return new Change( Change.Type.Removed, flow );
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
        Command connectWithFlow = new ConnectWithFlow();
        connectWithFlow.setArguments( (Map<String, Object>) get( "flowState" ) );
        // The flow being re-connected
        connectWithFlow.set( "flow", commander.resolveId( (Long) ( get( "flow" ) ) ));
        return connectWithFlow;
    }

}
