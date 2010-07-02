package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Plan;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 15, 2009
 * Time: 9:27:13 AM
 */
public class RemoveProducer extends AbstractCommand {

    public RemoveProducer() {
    }

    public RemoveProducer( String username ) {
        set( "producer", username );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Vote not to put in production";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        String producer = (String) get( "producer" );
        Plan plan = User.plan();
        plan.removeProducer( producer );
        return new Change( Change.Type.Updated, plan, "producers" );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean forcesSnapshot() {
        return true;
    }

}
