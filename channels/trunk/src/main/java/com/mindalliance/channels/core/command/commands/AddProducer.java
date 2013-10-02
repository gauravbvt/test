/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Plan;

/**
 * Adds a producer to a plan (a planner voting for it to go into production).
 */
public class AddProducer extends AbstractCommand {

    public AddProducer() {
        super( "daemon" );
    }

    public AddProducer( String userName, String producer ) {
        super( userName );
        set( "producer", producer );
    }

    @Override
    public String getName() {
        return "vote to put in production";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        String producer = (String) get( "producer" );
        Plan plan = commander.getPlan();
        PlanManager planManager = commander.getCommunityService().getPlanService().getPlanManager();
        boolean allInFavor = planManager.addProducer( producer, plan );
        setTargetDescription( producer );
        Change change = new Change( Change.Type.Updated, plan, "producers" );
        if ( allInFavor ) {
            change.setMessage( "All developers are in favor of putting this version into production" );
        }
        return change;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        return null;
    }

    @Override
    public boolean forcesSnapshot() {
        return true;
    }

}
