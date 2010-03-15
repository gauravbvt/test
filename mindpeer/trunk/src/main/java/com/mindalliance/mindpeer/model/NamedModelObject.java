// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import javax.persistence.MappedSuperclass;

/**
 * A model object with a unique name.
 * Uniqueness constraint specified in subclasses.
 */
@MappedSuperclass
public abstract class NamedModelObject extends ModelObject
        implements Countable, Comparable<NamedModelObject> {

    private static final long serialVersionUID = 8059558465645800831L;

    private String name;

    private String description;

    /**
     * Create a new NamedModelObject instance.
     */
    protected NamedModelObject() {
        this( "unnamed" );
    }

    /**
     * Create a new NamedModelObject instance.
     *
     * @param name the given name
     */
    protected NamedModelObject( String name ) {
        this.name = name;
    }

    /**
     * Return the description.
     * @return the value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this object.
     * @param description the new description value.
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * Return the name.
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this object.
     * @param name the new name value.
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Compares this object with the specified object for order.
     *
     * @param   o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this object.
     */
    public int compareTo( NamedModelObject o ) {
        return name.compareTo( o.getName() );
    }
}
