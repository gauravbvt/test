// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import org.apache.wicket.IClusterable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Base class for persisted objects.
 */
@MappedSuperclass
public abstract class ModelObject implements IClusterable {

    private static final long serialVersionUID = -1730275307746042188L;

    /** Timestamp of the object's creation. */
    @Temporal( TemporalType.TIMESTAMP )
    private Date created;

    /** Date the object was last modified. */
    @Temporal( TemporalType.TIMESTAMP )
    private Date lastModified;

    /** The internal user id. */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Create a new ModelObject instance.
     */
    protected ModelObject() {
        Date now = new Date();
        lastModified = now;
        created = now;
    }

    /**
     * Equality test.
     * @param obj an object to compare against
     * @return true if a model object with the same id.
     */
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;

        if ( obj instanceof ModelObject ) {
            Long thatId = ( (ModelObject) obj ).getId();
            return id == null ? thatId == null : id.equals( thatId );
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    /**
     * Method getId returns the id of this ModelObject object.
     * @return the id (type Long) of this ModelObject object.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id of this ModelObject.
     * @param id the new id value.
     */
    public void setId( Long id ) {
        this.id = id;
    }

    /**
     * Return the ModelObject's created.
     * @return the value of created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Sets the created of this ModelObject.
     * @param created the new created value.
     */
    protected void setCreated( Date created ) {
        this.created = created;
    }

    /**
     * Return the ModelObject's lastModified.
     * @return the value of lastModified
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Sets the lastModified of this ModelObject.
     * @param lastModified the new lastModified value.
     */
    public void setLastModified( Date lastModified ) {
        this.lastModified = lastModified;
    }
}
