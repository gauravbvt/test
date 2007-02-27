// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.beans.PropertyVetoException;

import com.mindalliance.channels.util.GUID;

/**
 * An object that has a unique name for all instances of the class.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public abstract class AbstractNamedObject extends AbstractModelObject {

    private String name;

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    public AbstractNamedObject( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the value of name.
     * @param name The new value of name
     * @throws PropertyVetoException when new name would conflict
     * with other objects.
     */
    public void setName( String name ) throws PropertyVetoException {
        this.name = name;
    }
}
