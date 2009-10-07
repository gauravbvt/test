package com.mindalliance.channels.model;

/**
 * An entity type.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 7, 2009
 * Time: 1:07:51 PM
 */
public class EntityType extends ModelEntity {
    /**
     * The domain of this entity type (Event, Role etc.)
     */
    private String domain;

    public EntityType() {
    }

    public EntityType( String name ) {
        super( name );
    }

    public String getDomain() {
        return domain;
    }

    /**
     * Set the entity type's domain from an entity.
     *
     * @param entity an entity
     */
    public void setDomainFrom( ModelEntity entity ) {
        assert domain == null;
        domain = entity.getClass().getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isType() {
        return true;
    }

    public boolean appliesTo( ModelEntity entity ) {
        return domain.equals( entity.getClass().getSimpleName() );
    }
}
