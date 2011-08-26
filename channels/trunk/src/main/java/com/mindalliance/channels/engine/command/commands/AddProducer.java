package com.mindalliance.channels.engine.command.commands;

import com.mindalliance.channels.engine.command.AbstractCommand;
import com.mindalliance.channels.engine.command.Change;
import com.mindalliance.channels.engine.command.Command;
import com.mindalliance.channels.engine.command.CommandException;
import com.mindalliance.channels.engine.command.Commander;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Plan;

/**
 * Adds a producer to a plan (a planner voting for it to go into production).
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 14, 2009
 * Time: 1:51:29 PM
 */
public class AddProducer extends AbstractCommand {

    public AddProducer() {
    }

    public AddProducer( String username ) {
        set( "producer", username );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "vote to put in production";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        String producer = (String) get( "producer" );
        Plan plan = User.plan();
        PlanManager planManager = commander.getQueryService().getPlanManager();
        boolean produced = planManager.addProducer( producer, plan );
        setTargetDescription( producer );
        if ( produced ) {
            commander.setResyncRequired();
            return new Change( Change.Type.Recomposed, plan );
        } else {
            return new Change( Change.Type.Updated, plan, "producers" );
        }
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
