package com.mindalliance.channels.model;

import java.text.Collator;

/**
 * ...
 */
public abstract class NamedObject implements Comparable<NamedObject> {

    private long id;
    private String name = "";
    private String description = "";

    private static long Counter = 0L;

    //=============================
    protected NamedObject() {
        setId( incrementCounter() );
    }

    private static long incrementCounter() {
        return Counter++;
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

    public void setName( String name ) {
        if ( name == null )
            throw new IllegalArgumentException( "Name can't be null" );
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

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
    @Override
    public boolean equals( Object obj ) {
        return this == obj
            || obj instanceof NamedObject
                  && getId() == ( (NamedObject) obj ).getId();
    }

    @Override
    public int hashCode() {
        return (int) ( getId() ^ getId() >>> 32 );
    }

    @Override
    public String toString() {
        return getName();
    }
}
