// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.definitions;

import com.mindalliance.channels.data.support.AuditedObject;
import com.mindalliance.channels.data.support.GUID;

/**
 * Essentially, an object with a name and a description.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class NamedObject extends AuditedObject
    implements Described {

    private String name = "Anonymous";
    private String description = "";

    /**
     * Default constructor.
     */
    public NamedObject() {
    }

    /**
     * Default constructor.
     * @param guid the guid
     * @param name the name
     */
    public NamedObject( GUID guid,  String name ) {
        super( guid );
        if ( name != null )
            setName( name );
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public NamedObject( GUID guid ) {
        super( guid );
    }

    /**
     * Utility constructor.
     * @param name the name
     */
    public NamedObject( String name ) {
        this( null, name );
    }

    /**
     * Sort according to name.
     * @param o the other data to compare to.
     */
    public int compareTo( Described o ) {
        return name.compareTo( o.getName() );
    }

    /**
     * Return the value of description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of description.
     * @param description The new value of description
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * Return the value of name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name.
     * @param name The new value of name
     */
    public void setName( String name ) {
        this.name = name;
    }

    /** Provide a printed form. */
    @Override
    public String toString() {
        return getName();
    }
}
