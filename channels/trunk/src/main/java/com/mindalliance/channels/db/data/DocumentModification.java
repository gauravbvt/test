package com.mindalliance.channels.db.data;

import java.util.Date;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/25/13
 * Time: 2:09 PM
 */
public class DocumentModification {

    private String username;
    private Date date;

    public DocumentModification() {
    }

    public DocumentModification( Date date, String username ) {
        this.date = date;
        this.username = username;
    }

    public Date getDate() {
        return date;
    }

    public void setDate( Date date ) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }
}
