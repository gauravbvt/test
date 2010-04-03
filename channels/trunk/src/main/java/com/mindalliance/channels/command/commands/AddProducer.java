package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Plan;

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
        return "Vote to put in production";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        String producer = (String) get( "producer" );
        Plan plan = PlanManager.plan();
        PlanManager planManager = commander.getQueryService().getPlanManager();
        boolean produced = planManager.addProducer( producer, plan );
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
