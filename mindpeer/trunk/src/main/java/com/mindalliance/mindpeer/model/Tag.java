// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import java.io.Serializable;
import java.util.Set;

/**
 * A persistent tag.
 */
@Entity
public class Tag implements Serializable, Comparable<Tag> {

    @Id
    @GeneratedValue
    private long id;

    @Column( unique = true )
    private String description;

    @ManyToMany( cascade = { CascadeType.MERGE, CascadeType.PERSIST },
                 mappedBy = "interests" )
    @OrderBy( "name" )
    private Set<Profile> profiles;

    /**
     * Create a new Tag instance.
     *
     * @param description the given description
     */
    public Tag( String description ) {
        if ( description == null )
            throw new IllegalArgumentException();
        this.description = description;
    }

    /**
     * Create a new Tag instance.
     */
    protected Tag() {
    }

    /**
     * Return the Tag's description.
     * @return the value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the Tag's id.
     * @return the value of id
     */
    public long getId() {
        return id;
    }

    /**
     * Set the id. Called by the persistence engine.
     * @param id the id
     */
    protected void setId( long id ) {
        this.id = id;
    }

    /**
     * Test if this tag is the same as another object.
     *
     * @param obj the given object
     * @return true if ids match
     */
    @Override
    public boolean equals( Object obj ) {
        return this == obj
            || obj != null
               && getClass() == obj.getClass()
               && id == ( (Tag) obj ).getId();
    }

    /**
     * ...
     *
     * @return int
     */
    @Override
    public int hashCode() {
        return (int) ( id ^ id >>> 32 );
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param   o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this object.
     */
    public int compareTo( Tag o ) {
        return description.compareTo( o.getDescription() );
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Tag[" + description + ']';
    }

    /**
     * Return the profiles that have this tag included in their interest.
     * @return the profiles
     */
    public Set<Profile> getProfiles() {
        return profiles;
    }

    /**
     * Set the profiles associated with this tag. Set by the persistence engine.
     * @param profiles the profiles
     */
    protected void setProfiles( Set<Profile> profiles ) {
        this.profiles = profiles;
    }
}
