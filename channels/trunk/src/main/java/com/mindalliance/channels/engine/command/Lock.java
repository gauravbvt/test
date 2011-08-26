package com.mindalliance.channels.engine.command;

import java.util.Date;

/**
 * A write lock on a model object.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 28, 2009
 * Time: 2:23:57 PM
 */
public class Lock {

    /**
     * Id of model object locked.
     */
    private final long id;

    /**
     * The name of the user who owns the lock.
     */
    private final String userName;

    /**
     * The date the lock was acquired.
     */
    private final Date date;

    public Lock( String userName, long id ) {
        this.userName = userName;
        this.id = id;
        date = new Date();
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public Date getDate() {
        return new Date( date.getTime() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Lock: " + id + ',' + userName + ',' + date;
    }
}
