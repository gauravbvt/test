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
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.command.commands.UpdateObject.Action;
import com.mindalliance.channels.core.dao.DefinitionManager;
import com.mindalliance.channels.core.dao.PlanDefinition;
import com.mindalliance.channels.core.model.Plan;

/**
 * Rename a plan and its plan definition.
 */
public class PlanRename extends AbstractCommand {

    public PlanRename() {
        super( "daemon" );
    }

    public PlanRename( String userName, Plan plan, String newName ) {
        super( userName );
        needLockOn( plan );
        set( "id", plan.getId() );
        set( "old", plan.getName() );
        set( "new", newName );
    }

    /**
     * The command's name.
     *
     * @return a string
     */
    @Override
    public String getName() {
        return "rename plan";
    }

    /**
     * Execute the command.
     *
     * @param commander a commander executing the command
     * @return change caused
     * @throws CommandException if execution fails
     */
    @Override
    public Change execute( Commander commander ) throws CommandException {
        Plan plan = getPlan( commander );

        Change result = getSubCommands( plan ).execute( commander );
        ignoreLock( plan.getId() );

        return result;
    }

    private MultiCommand getSubCommands( Plan plan ) {
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            String newName = (String) get( "new" );
            multi = new MultiCommand( getUserName(), "rename plan - extra" );
            multi.addCommand( new PlanDefinitionRename( getUserName(), plan, newName ) );
            multi.addCommand( new UpdatePlanObject( getUserName(), plan, "name", newName, Action.Set ) );
            set( "subCommands", multi );
        }

        return multi;
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
     * @throws CommandException if failed to make undo command
     */
    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        return new PlanRename( getUserName(), getPlan( commander ), (String) get( "old" ) );
    }

    private Plan getPlan( Commander commander ) throws CommandException {
        return commander.resolve( Plan.class, (Long) get( "id" ) );
    }

    //==================================================================

    /**
     * Rename a plan in a plan definition.
     */
    public static class PlanDefinitionRename extends AbstractCommand {

        public PlanDefinitionRename( String userName, Plan plan, String name ) {
            super( userName );
            set( "uri", plan.getUri() );
            set( "old", plan.getName() );
            set( "new", name );
        }

        @Override
        public String getName() {
            return "rename a plan definition";
        }

        @Override
        public Change execute( Commander commander ) throws CommandException {
            DefinitionManager definitionManager = commander.getQueryService().getPlanManager().getDefinitionManager();

            PlanDefinition planDefinition = definitionManager.get( (String) get( "uri" ) );
            planDefinition.setName( (String) get( "new" ) );

            return new Change( Type.Updated, "plan.properties" );
        }

        @Override
        public boolean isUndoable() {
            return true;
        }

        @Override
        protected Command makeUndoCommand( Commander commander ) throws CommandException {
            return new PlanDefinitionRename( getUserName(), commander.getPlan(), (String) get( "old" ) );
        }
    }
}
