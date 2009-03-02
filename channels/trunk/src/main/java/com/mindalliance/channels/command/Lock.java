package com.mindalliance.channels.command;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.pages.Project;

import java.util.Date;

/**
 * A lock on a model object.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 28, 2009
 * Time: 2:23:57 PM
 */
public class Lock {

    /**
     * Whether write or read lock.
     */
    private boolean write;
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
     * @param write       whtehr write or read lock
     * @param modelObject the model object to be locked
     */
    public Lock( boolean write, ModelObject modelObject ) {
        this.write = write;
        modelObjectId = modelObject.getId();
        userName = Project.getUserName();
        date = new Date();
    }

    public boolean isWrite() {
        return write;
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

}
