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
import com.mindalliance.channels.core.dao.ModelDefinition;
import com.mindalliance.channels.core.dao.ModelDefinitionManager;
import com.mindalliance.channels.core.model.CollaborationModel;

/**
 * Set the plan's client field in the plans.properties file.
 */
public class ModelSetClient extends AbstractCommand {

    public ModelSetClient() {
        super( "daemon" );
    }

    public ModelSetClient( String userName, CollaborationModel collaborationModel, String newClient ) {
        super( userName );
        needLockOn( collaborationModel );
        set( "uri", collaborationModel.getUri() );
        set( "old", collaborationModel.getClient() );
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
        ModelDefinitionManager modelDefinitionManager = commander.getQueryService().getModelManager().getModelDefinitionManager();

        ModelDefinition modelDefinition = modelDefinitionManager.get( (String) get( "uri" ) );
        modelDefinition.setClient( (String) get( "new" ) );

        return new Change( Type.Updated, "model.properties" );
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
        return new ModelSetClient( getUserName(), commander.getPlan(), (String) get( "old" ) );
    }
}
