package com.mindalliance.channels;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.text.Collator;
import java.util.Date;

/**
 * An object with name, id and description, comparable by its toString() values.
 */
@Entity @Inheritance( strategy = InheritanceType.JOINED )
public abstract class ModelObject implements Serializable, Comparable<ModelObject>, Identifiable {

    /** Cheap way of creating unique default ids. Overloaded by hibernate eventually. */
    private static long Counter = 1L ;

    /** Unique id of this object. */
    private long id;

    /** Name of this object. */
    private String name = "";

    /** The description. */
    private String description = "";

    /** Time the object was last modified. Set by aspect. */
    private Date lastModified;

    //=============================
    protected ModelObject() {
        setId( Counter++ );
    }

    /**
     * Utility constructor for tests.
     * @param name the name of the new object
     */
    protected ModelObject( String name ) {
        this();
        setName( name );
    }

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    public long getId() {
        return id;
    }

    public final void setId( long id ) {
        this.id = id;
    }

    /**
     * @return the name of the flow
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this object.
     * @param name the name. Will complain if null.
     */
    public void setName( String name ) {
        this.name = name == null ? "" : name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Set the description of this object.
     * @param description the description. Will set to empty string if null.
     */
    public void setDescription( String description ) {
        this.description = description == null ? "" : description;
    }

    //=============================
    /**
     * Compare with another named object.
     * @param o the object.
     * @return 0 if equals, -1 if this object smaller than the other, 1 if greater
     */
    public int compareTo( ModelObject o ) {
        int result = Collator.getInstance().compare( toString(), o.toString() );
        if ( result == 0 )
            result = getId() > o.getId() ? 1
                   : getId() < o.getId() ? -1
                   : 0;
        return result;
    }

    //=============================
    /** {@inheritDoc} */
    @Override
    public boolean equals( Object obj ) {
        return this == obj
            || obj instanceof ModelObject
                  && id == ( (ModelObject) obj ).getId();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Long.valueOf( id ).hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }

    @Transient
    public Date getLastModified() {
        // TODO implement last modified with aspect
        return new Date();
    }

    /**
     * Get a label
     * @return a string
     */
    @Transient
    public String getLabel() {
        return getName();
    }

    /**
     * Whether the model object is an entity
     * @return a boolean
     */
    @Transient
    public boolean isEntity() {
        return false;
    }

}
