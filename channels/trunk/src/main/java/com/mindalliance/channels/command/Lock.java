package com.mindalliance.channels.command;

import com.mindalliance.channels.pages.Project;

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
    private long modelObjectId;
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
     * @param id the id of the model object to be locked
     */
    public Lock( long id ) {
        modelObjectId = id;
        userName = Project.getUserName();
        date = new Date();
    }

    public long getModelObjectId() {
        return modelObjectId;
    }

    public String getUserName() {
        return userName;
    }

    public Date getDate() {
        return date;
    }

    /**
     * Is lock owned by user with given name?
     *
     * @param userName a user name
     * @return a boolean
     */
    public boolean isOwnedBy( String userName ) {
        return userName.equals( userName );
    }
}
