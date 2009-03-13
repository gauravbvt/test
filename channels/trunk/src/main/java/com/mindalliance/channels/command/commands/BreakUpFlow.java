package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.Commander;

/**
 * Command to break up a given flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 7:21:58 PM
 */
public class BreakUpFlow extends AbstractCommand {

    public BreakUpFlow( Flow flow ) {
        super();
        addConflicting( flow );
        needLocksOn( CommandUtils.getLockingSetFor( flow ) );
        needLockOn( flow.getScenario() );
        setArguments( CommandUtils.getFlowState( flow ) );
        addArgument( "flow", flow.getId() );
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
    public Object execute( Commander commander ) throws CommandException {
        Service service = commander.getService();
        try {
            Scenario scenario = service.find( Scenario.class, (Long) get( "scenario" ) );
            Flow flow = scenario.findFlow( (Long) get( "flow" ) );
            // TODO - move logic here
            // Make sure undo is symetrical (disconnect flows created during breakup)
            flow.breakup();
            ignoreLock( (Long) get( "flow" ) );
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
        ConnectWithFlow connectWithFlow = new ConnectWithFlow();
        connectWithFlow.setArguments( getArguments() );
        return connectWithFlow;
    }

}
