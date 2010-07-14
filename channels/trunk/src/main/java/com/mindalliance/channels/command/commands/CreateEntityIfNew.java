package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.query.QueryService;

/**
 * Create an entity if it does not already exist.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 11, 2009
 * Time: 11:40:24 AM
 */
public class CreateEntityIfNew extends AbstractCommand {

    public CreateEntityIfNew() {
    }

    public <T extends ModelEntity> CreateEntityIfNew( Class<T> clazz, String name, ModelEntity.Kind kind ) {
        set( "class", clazz.getName() );
        set( "name", name );
        set( "kind", kind.name() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "create new " + getSimpleSort();
    }

    private String getSimpleSort() {
        try {
            Class clazz = Class.forName( (String) get( "class" ) );
            ModelEntity.Kind kind = ModelEntity.Kind.valueOf( (String)get("kind") );
            return ( clazz.getSimpleName()
                    + ( clazz != Role.class && clazz != Event.class && kind == ModelEntity.Kind.Type
                        ? " type "
                        : " ") ).toLowerCase();
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        try {
            Class<ModelEntity> clazz = (Class<ModelEntity>) Class.forName( (String) get( "class" ) );
            String name = (String) get( "name" );
            ModelEntity.Kind kind = ModelEntity.Kind.valueOf( (String) get( "kind" ) );
            boolean exists = queryService.entityExists( clazz, name, kind );
            ModelEntity entity;
            Long priorId = (Long) get( "id" );
            if ( kind == ModelEntity.Kind.Actual ) {
                entity = queryService.safeFindOrCreate( clazz, name, priorId );
            } else {
                entity = queryService.safeFindOrCreateType( clazz, name, priorId );
            }
            set( "id", entity.getId() );
            describeTarget( entity );
            return exists
                    ? new Change( Change.Type.None, entity )
                    : new Change( Change.Type.Added, entity );
        } catch ( Exception e ) {
            throw new CommandException( "Failed to create entity", e );
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

}
