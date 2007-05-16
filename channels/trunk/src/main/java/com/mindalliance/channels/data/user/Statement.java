/*
 * Created on May 3, 2007
 */
package com.mindalliance.channels.data.user;

import java.io.Serializable;
import java.util.Date;
import com.mindalliance.channels.data.system.System;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.Unique;
import com.mindalliance.channels.util.GUID;

/**
 * A statement made by a user
 * 
 * @author jf
 */
public abstract class Statement implements Serializable, Unique {

    private GUID guid;
    private User user;
    private Date when;
    private String content;

    public Statement() {
        super();
        guid = System.guidFactory.newGuid();
    }

    /**
     * @return the guid
     */
    public GUID getGuid() {
        return guid;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent( String content ) {
        this.content = content;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser( User user ) {
        this.user = user;
    }

    /**
     * @return the when
     */
    public Date getWhen() {
        return when;
    }

    /**
     * @param when the when to set
     */
    public void setWhen( Date when ) {
        this.when = when;
    }

    /**
     * @param guid the guid to set
     */
    public void setGuid( GUID guid ) {
        this.guid = guid;
    }
}
