package com.mindalliance.channels.db.data;

import java.io.Serializable;
import java.util.Date;

/**
 * A lock on persistent data.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/21/14
 * Time: 10:23 AM
 */
public class DataLock implements Serializable {

    private Date whenAcquired;
    private String username;

    public DataLock( String username ) {
        this.username = username;
        whenAcquired = new Date();
    }

    public String getUsername() {
        return username;
    }

    public Date getWhenAcquired() {
        return whenAcquired;
    }

    public void refresh() {
        whenAcquired = new Date();
    }

    public boolean isOwnedBy( String username ) {
        return this.username.equals( username );
    }

    public boolean isExpired( long timeout ) {
        return whenAcquired.getTime() + timeout < new Date().getTime();
    }
}
