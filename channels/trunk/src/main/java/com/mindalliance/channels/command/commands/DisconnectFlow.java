package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Service;
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

    public DisconnectFlow( Flow flow ) {
        addConflicting( flow );
        needLocksOn( CommandUtils.getLockingSetFor( flow ) );
        addArgument( "scenario", flow.getScenario().getId() );
        addArgument( "flow", flow.getId() );
        addArgument( "flowState", CommandUtils.getFlowState( flow ) );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "disconnect flow";
    }

    /**
     * {@inheritDoc}
     */
    public Object execute( Commander commander ) throws CommandException {
        Service service = commander.getService();
        try {
            Scenario scenario = service.find( Scenario.class, (Long) get( "scenario" ) );
            Flow flow = scenario.findFlow( (Long) get( "flow" ) );
            flow.disconnect();
            return null;
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
        return connectWithFlow;
    }

}
