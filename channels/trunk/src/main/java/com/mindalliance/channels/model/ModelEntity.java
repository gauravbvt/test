package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

/**
 * An entity model object.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 7, 2009
 * Time: 1:05:30 PM
 */
public abstract class ModelEntity extends ModelObject {
    /**
     * Type set.
     */
    private List<EntityType> types = new ArrayList<EntityType>();

    public ModelEntity() {
    }

    public ModelEntity( String name ) {
        super( name );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEntity() {
        return true;
    }

    /**
     * Whether this entity is a type and not an actual entity.
     *
     * @return a boolean
     */
    public boolean isType() {
        // Default
        return false;
    }

    public List<EntityType> getTypes() {
        return types;
    }

    public void setTypes( List<EntityType> types ) {
        this.types = types;
    }

    public void addType( EntityType type ) {
        assert type.appliesTo( this );
        if ( !types.contains( type ) ) types.add( type );
    }
}
