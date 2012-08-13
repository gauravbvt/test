/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Change.Type;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.dao.DefinitionManager;
import com.mindalliance.channels.core.dao.PlanDefinition;
import com.mindalliance.channels.core.model.Plan;

/**
 * Set the plan's client field in the plans.properties file.
 */
public class PlanSetClient extends AbstractCommand {

    public PlanSetClient() {
        super( "daemon" );
    }

    public PlanSetClient( String userName, Plan plan, String newClient ) {
        super( userName );
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
    @Override
    public String getName() {
        return "set the persistent client information";
    }

    /**
     * Execute the command.
     *
     * @param commander a commander executing the command
     * @return change caused
     */
    @Override
    public Change execute( Commander commander ) {
        DefinitionManager definitionManager = commander.getQueryService().getPlanManager().getDefinitionManager();

        PlanDefinition planDefinition = definitionManager.get( (String) get( "uri" ) );
        planDefinition.setClient( (String) get( "new" ) );

        return new Change( Type.Updated, "plan.properties" );
    }

    /**
     * Whether the command can be undone.
     *
     * @return a boolean
     */
    @Override
    public boolean isUndoable() {
        return true;
    }

    /**
     * Make an undo command.
     *
     * @param commander a a commander
     * @return a command
     */
    @Override
    protected Command makeUndoCommand( Commander commander ) {
        return new PlanSetClient( getUserName(), commander.getPlan(), (String) get( "old" ) );
    }
}
