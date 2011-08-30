/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL.
 */

package com.mindalliance.channels.social;

import com.mindalliance.channels.core.PersistentObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * A generic persistent object.
 */
public class AbstractPersistentObject implements PersistentObject {

    private final Date date;
    private final String id;

    public AbstractPersistentObject() {
        date = new Date();
        id = UUID.randomUUID().toString();
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getId() {
        return id;
    }

    public String toString() {
        return "at " + DateFormat.getInstance().format( date );
    }
}
