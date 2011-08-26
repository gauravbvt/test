// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.engine.command.commands;

import com.mindalliance.channels.engine.command.AbstractCommand;
import com.mindalliance.channels.engine.command.Change;
import com.mindalliance.channels.engine.command.Command;
import com.mindalliance.channels.engine.command.CommandException;
import com.mindalliance.channels.engine.command.Commander;
import com.mindalliance.channels.engine.command.MultiCommand;
import com.mindalliance.channels.core.dao.DefinitionManager;
import com.mindalliance.channels.core.dao.PlanDefinition;
import com.mindalliance.channels.core.model.Plan;

/**
 * Rename a plan and its plan definition.
 */
public class PlanRename extends AbstractCommand {

    public PlanRename() {
    }

    public PlanRename( Plan plan, String newName ) {
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
            multi = new MultiCommand( "rename plan - extra" );
            multi.addCommand(
                    new PlanDefinitionRename( plan, newName ) );
            multi.addCommand(
                    new UpdatePlanObject( plan, "name", newName, UpdateObject.Action.Set ) );
            set( "subCommands", multi );
        }

        return multi;
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
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        return new PlanRename( getPlan( commander ), (String) get( "old" ) );
    }

    private Plan getPlan( Commander commander ) throws CommandException {
        return commander.resolve( Plan.class, (Long) get( "id" ) );
    }


    //==================================================================
    /**
     * Rename a plan in a plan definition.
     */
    public static class PlanDefinitionRename extends AbstractCommand {

        public PlanDefinitionRename() {
        }

        public PlanDefinitionRename( Plan plan, String name ) {
            set( "uri", plan.getUri() );
            set( "old", plan.getName() );
            set( "new", name );
        }

        /** {@inheritDoc} */
        public String getName() {
            return "rename a plan definition";
        }

        /** {@inheritDoc} */
        public Change execute( Commander commander ) throws CommandException {
            DefinitionManager definitionManager =
                    commander.getQueryService().getPlanManager().getDefinitionManager();

            PlanDefinition planDefinition = definitionManager.get( (String) get( "uri" ) );
            planDefinition.setName( (String) get( "new" ) );

            return new Change( Change.Type.Updated, "plan.properties" );
        }

        /** {@inheritDoc} */
        public boolean isUndoable() {
            return true;
        }

        /** {@inheritDoc} */
        @Override
        protected Command makeUndoCommand( Commander commander ) throws CommandException {
            return new PlanDefinitionRename( commander.getPlan(), (String) get( "old" ) );
        }
    }
}
