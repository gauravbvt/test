// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.dao.DefinitionManager;
import com.mindalliance.channels.core.dao.PlanDefinition;
import com.mindalliance.channels.core.model.Plan;

/**
 * Set the plan's client field in the plans.properties file.
 */
public class PlanSetClient extends AbstractCommand {

    public PlanSetClient() {
    }

    public PlanSetClient( Plan plan, String newClient ) {
        needLockOn( plan );
        set( "uri", plan.getUri() );
        set( "old", plan.getClient() );
        set( "new", newClient );
    }

    /**
     * The command's name.
     *
     * @return a string
     */
    public String getName() {
        return "set the persistent client information";
    }

    /**
     * Execute the command.
     *
     * @param commander a commander executing the command
     * @return change caused
     * @throws CommandException if execution fails
     */
    public Change execute( Commander commander ) {
        DefinitionManager definitionManager =
                commander.getQueryService().getPlanManager().getDefinitionManager();

        PlanDefinition planDefinition = definitionManager.get( (String) get( "uri" ) );
        planDefinition.setClient( (String) get( "new" ) );

        return new Change( Change.Type.Updated, "plan.properties" );
    }

    /**
     * Whether the command can be undone.
     *
     * @return a boolean
     */
    public boolean isUndoable() {
        return true;
    }

    /**
     * Make an undo command.
     *
     * @param commander a a commander
     * @return a command
     * @throws CommandException if failed to make undo command
     */
    @Override
    protected Command makeUndoCommand( Commander commander ) {
        return new PlanSetClient( commander.getPlan(), (String) get( "old" ) );
    }
}
