package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Scenario;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 6, 2009
 * Time: 9:11:25 AM
 */
public class RemoveCapability extends AbstractCommand {

    public RemoveCapability() {
    }

    public RemoveCapability( Flow flow ) {
        addConflicting( flow );
        needLocksOn( CommandUtils.getLockingSetFor( flow ) );
        setArguments( CommandUtils.getFlowState( flow ) );
        set( "flow", flow.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "remove sharing capability";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Flow flow = scenario.findFlow( (Long) get( "flow" ) );
            flow.disconnect();
            commander.releaseAnyLockOn( flow );
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
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        AddCapability command = new AddCapability();
        command.setArguments( getArguments() );
        return command;
    }
}
