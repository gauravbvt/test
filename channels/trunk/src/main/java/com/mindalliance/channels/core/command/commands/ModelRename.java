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
import com.mindalliance.channels.core.dao.ModelDefinition;
import com.mindalliance.channels.core.dao.ModelDefinitionManager;
import com.mindalliance.channels.core.model.CollaborationModel;

/**
 * Rename a plan and its plan definition.
 */
public class ModelRename extends AbstractCommand {

    public ModelRename() {
        super( "daemon" );
    }

    public ModelRename( String userName, CollaborationModel collaborationModel, String newName ) {
        super( userName );
        needLockOn( collaborationModel );
        set( "id", collaborationModel.getId() );
        set( "old", collaborationModel.getName() );
        set( "new", newName );
    }

    /**
     * The command's name.
     *
     * @return a string
     */
    @Override
    public String getName() {
        return "rename model";
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
        CollaborationModel collaborationModel = getPlan( commander );

        Change result = getSubCommands( collaborationModel ).execute( commander );
        ignoreLock( collaborationModel.getId() );

        return result;
    }

    private MultiCommand getSubCommands( CollaborationModel collaborationModel ) {
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            String newName = (String) get( "new" );
            multi = new MultiCommand( getUserName(), "rename model - extra" );
            multi.addCommand( new ModelDefinitionRename( getUserName(), collaborationModel, newName ) );
            multi.addCommand( new UpdateModelObject( getUserName(), collaborationModel, "name", newName, Action.Set ) );
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
        return new ModelRename( getUserName(), getPlan( commander ), (String) get( "old" ) );
    }

    private CollaborationModel getPlan( Commander commander ) throws CommandException {
        return commander.resolve( CollaborationModel.class, (Long) get( "id" ) );
    }

    //==================================================================

    /**
     * Rename a plan in a plan definition.
     */
    public static class ModelDefinitionRename extends AbstractCommand {

        public ModelDefinitionRename() {}

        public ModelDefinitionRename( String userName, CollaborationModel collaborationModel, String name ) {
            super( userName );
            set( "uri", collaborationModel.getUri() );
            set( "old", collaborationModel.getName() );
            set( "new", name );
        }

        @Override
        public String getName() {
            return "rename a model definition";
        }

        @Override
        public Change execute( Commander commander ) throws CommandException {
            ModelDefinitionManager modelDefinitionManager = commander.getQueryService().getModelManager().getModelDefinitionManager();

            ModelDefinition modelDefinition = modelDefinitionManager.get( (String) get( "uri" ) );
            modelDefinition.setName( (String) get( "new" ) );

            return new Change( Type.Updated, "model.properties" );
        }

        @Override
        public boolean isUndoable() {
            return true;
        }

        @Override
        protected Command makeUndoCommand( Commander commander ) throws CommandException {
            return new ModelDefinitionRename( getUserName(), commander.getPlan(), (String) get( "old" ) );
        }
    }
}
