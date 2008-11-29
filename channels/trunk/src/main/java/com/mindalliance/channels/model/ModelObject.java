package com.mindalliance.channels.model;

import java.io.Serializable;
import java.text.Collator;

/**
 * An object with name, id and description, comparable by its toString() values.
 */
public abstract class ModelObject implements Serializable, Comparable<ModelObject> {

    /** Cheap way of creating unique default ids. Overloaded by hibernate eventually. */
    private static long Counter = 1L ;

    /** Unique id of this object. */
    private long id;

    /** Name of this object. */
    private String name = "";

    /** The description. */
    private String description = "";

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

    public long getId() {
        return id;
    }

    private void setId( long id ) {
        this.id = id;
    }

    public final String getName() {
        return name;
    }

    /**
     * Set the name of this object.
     * @param name the name. Will complain if null.
     */
    public final void setName( String name ) {
        if ( name == null )
            throw new IllegalArgumentException( "Name can't be null" );
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Set the description of this object.
     * @param description the description. Will set to empty string if null.
     */
    public void setDescription( String description ) {
        if ( description == null )
            throw new IllegalArgumentException( "Description can't be null" );
        this.description = description;
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
        return (int) id;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }
}
