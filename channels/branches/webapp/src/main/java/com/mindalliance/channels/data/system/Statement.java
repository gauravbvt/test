// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import java.io.Serializable;
import java.util.Date;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.support.GUID;
import com.mindalliance.channels.data.support.Unique;

/**
 * A statement made by a user.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class Statement implements Serializable, Unique {

    private GUID guid;
    private User user;
    private Date when;
    private String content;

    /**
     * Default constructor.
     */
    public Statement() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid of the statement.
     */
    public Statement( GUID guid ) {
        this();
        setGuid( guid );
    }

    /**
     * Return the guid.
     */
    public GUID getGuid() {
        return guid;
    }

    /**
     * Return the content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Set the content.
     * @param content the content to set
     */
    public void setContent( String content ) {
        this.content = content;
    }

    /**
     * Return the user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Set the user.
     * @param user the user to set
     */
    public void setUser( User user ) {
        this.user = user;
    }

    /**
     * Return the date of this statement.
     */
    public Date getWhen() {
        return when;
    }

    /**
     * Set the date of this statement.
     * @param when the when to set
     */
    public void setWhen( Date when ) {
        this.when = when;
    }

    /**
     * Set the guid.
     * <p>This method should only be called by the persistency layer.</p>
     * @param guid the guid to set
     */
    public void setGuid( GUID guid ) {
        this.guid = guid;
    }
}
