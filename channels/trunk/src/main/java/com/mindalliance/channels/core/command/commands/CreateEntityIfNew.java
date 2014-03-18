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
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelEntity.Kind;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.query.QueryService;

/**
 * Create an entity if it does not already exist.
 */
public class CreateEntityIfNew extends AbstractCommand {

    public CreateEntityIfNew() {
        super( "daemon" );
    }

    public <T extends ModelEntity> CreateEntityIfNew( String userName, Class<T> clazz, String name, Kind kind ) {
        super( userName );
        set( "class", clazz.getName() );
        set( "name", name );
        set( "kind", kind.name() );
    }

    @Override
    public String getName() {
        return "create new " + getSimpleSort();
    }

    private String getSimpleSort() {
        try {
            Class clazz = Class.forName( (String) get( "class" ) );
            ModelEntity.Kind kind = ModelEntity.Kind.valueOf( (String) get( "kind" ) );
            return ( clazz.getSimpleName() + (
                    clazz != Role.class && clazz != Event.class && kind == ModelEntity.Kind.Type ?
                    " type " :
                    " " ) ).toLowerCase();
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        try {
            Class<ModelEntity> clazz = (Class<ModelEntity>) Class.forName( (String) get( "class" ) );
            String name = (String) get( "name" );
            ModelEntity.Kind kind = ModelEntity.Kind.valueOf( (String) get( "kind" ) );
            boolean exists = queryService.entityExists( clazz, name, kind );
            Long priorId = (Long) get( "id" );
            ModelEntity entity = kind == Kind.Actual ?
                                 queryService.safeFindOrCreate( clazz, name, priorId ) :
                                 queryService.safeFindOrCreateType( clazz, name, priorId );
            set( "id", entity.getId() );
            describeTarget( entity );
            return exists ? new Change( Change.Type.None, entity ) : new Change( Change.Type.Added, entity );
        } catch ( Exception e ) {
            throw new CommandException( "Failed to create entity", e );
        }
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
    public boolean triggersAfterCommand() {
        return false;    // Prevent clean up of new entity by issue scanner before it is referenced.
    }
}
