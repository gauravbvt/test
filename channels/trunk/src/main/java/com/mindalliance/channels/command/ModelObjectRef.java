package com.mindalliance.channels.command;

import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;

import java.io.Serializable;

/**
 * A reference to an Identifiable.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 8, 2009
 * Time: 7:34:16 PM
 */
public class ModelObjectRef implements Serializable {
    /**
     * A model object's id.
     */
    private long id;
    /**
     * The model object's class name
     */
    private String className;
    /**
     * The entity's name, if an entity is referenced.
     */
    private String entityName;
    /**
     * Kind of entity
     */
    private String entityKind;

    public ModelObjectRef( ModelObject mo ) {
        if ( mo.isEntity() ) {
            entityName = mo.getName();
            entityKind = ( (ModelEntity) mo ).getKind().name();
        }
        id = mo.getId();
        className = mo.getClass().getName();
    }

    public long getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    /**
     * Get model object class given class name.
     *
     * @return a class
     * @throws NotFoundException if class not found
     */
    @SuppressWarnings( "unchecked" )
    public Class<? extends ModelObject> getModelObjectClass() throws NotFoundException {
        try {
            return (Class<? extends ModelObject>) Class.forName( className );
        } catch ( ClassNotFoundException e ) {
            throw new NotFoundException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return className + ":" + id;
    }

    /**
     * Resolve the reference to a model object
     *
     * @param commander a commander
     * @return a model object
     * @throws NotFoundException if not found
     * @throws com.mindalliance.channels.command.CommandException
     *                           if commander fails to resolve
     */
    @SuppressWarnings( "unchecked" )
    public ModelObject resolve( Commander commander ) throws NotFoundException, CommandException {
        ModelObject mo;
        if ( entityName == null ) {
            mo = commander.resolve( getModelObjectClass(), id );
        } else {
            if ( ModelEntity.class.isAssignableFrom( getModelObjectClass() ) ) {
                if ( entityKind.equals( ModelEntity.Kind.Actual.name() ) ) {
                    mo = commander.getQueryService().findOrCreate(
                            (Class<ModelEntity>) getModelObjectClass(),
                            entityName,
                            id );
                }
                else {
                    mo = commander.getQueryService().findOrCreateType(
                            (Class<ModelEntity>) getModelObjectClass(),
                            entityName,
                            id );
                }
            } else {
                throw new NotFoundException();
            }
        }
        return mo;
    }
}