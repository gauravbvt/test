package com.mindalliance.channels.command;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.ModelObject;

import java.io.Serializable;

/**
 * A reference to an Identifiable.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 8, 2009
 * Time: 7:34:16 PM
 */
public class ModelObjectRef implements Serializable {

    private long id;
    private String className;

    public ModelObjectRef( ModelObject mo ) {
        id = mo.getId();
        className = mo.getClass().getName();
    }

    public long getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public Class<? extends ModelObject> getModelObjectClass() throws NotFoundException {
        try {
            return (Class<? extends ModelObject>) Class.forName( className );
        } catch ( ClassNotFoundException e ) {
            throw new NotFoundException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return className + ":" + id;
    }
}