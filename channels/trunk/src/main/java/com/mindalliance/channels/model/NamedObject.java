package com.mindalliance.channels.model;

import java.text.Collator;

/**
 * An object with name, id and description, comparable by its toString() values.
 */
public abstract class NamedObject implements Comparable<NamedObject> {

    private static long Counter = 1L ;

    private long id;
    private String name = "";
    private String description = "";

    //=============================
    protected NamedObject() {
        setId( Counter++ );
    }

    public long getId() {
        return id;
    }

    private void setId( long id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    /**
     * Set the name of this object.
     * @param name the name. Will complain if null.
     */
    public void setName( String name ) {
        if ( name == null )
            throw new IllegalArgumentException( "Name can't be null" );
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Set the description of this object.
     * @param description the description. Will complain if null.
     */
    public void setDescription( String description ) {
        if ( description == null )
            throw new IllegalArgumentException( "Description can't be null" );
        this.description = description;
    }

    //=============================
    public int compareTo( NamedObject o ) {
        return Collator.getInstance().compare( toString(), o.toString() );
    }

    //=============================
    /** {@inheritDoc} */
    @Override
    public boolean equals( Object obj ) {
        return this == obj
            || obj instanceof NamedObject
                  && id == ( (NamedObject) obj ).getId();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return (int) ( id ^ id >>> 32 );
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }
}
