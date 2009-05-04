package com.mindalliance.channels.command;

import com.mindalliance.channels.model.User;

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
    private long id;
    /**
     * The name of the user who owns the lock.
     */
    private String userName;
    /**
     * The date the lock was acquired.
     */
    private Date date;

    /**
     * Constructor.
     *
     * @param id the id of the identifiable to be locked
     */
    public Lock( long id ) {
        this.id = id;
        userName = User.current().getName();
        date = new Date();
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    // For debugging use only
    public void setUserName( String userName ) {
        this.userName = userName;
    }

    public Date getDate() {
        return date;
    }

    /**
     * Is lock owned by user with given name?
     *
     * @param name a user name
     * @return a boolean
     */
    public boolean isOwnedBy( String name ) {
        return userName.equals( name );
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "Lock: " + id + "," + userName + "," + date;
    }
}
