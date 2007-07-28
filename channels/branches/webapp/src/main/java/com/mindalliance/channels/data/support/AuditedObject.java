// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.support;

import java.util.Date;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;

import com.mindalliance.channels.User;

/**
 * A unique object with an audit trail.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public abstract class AuditedObject extends AbstractJavaBean
    implements Unique, Element {

    private User lastModifier;
    private Date lastModified;
    private GUID guid;
    private User creator;
    private Date created;

    /**
     * Default constructor.
     */
    public AuditedObject() {
        super();

        Date date = new Date();
        this.created = date;
        this.lastModified = date;

        User user = getCurrentUser();
        this.creator = user;
        this.lastModifier = user;
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public AuditedObject( GUID guid ) {
        this();
        this.guid = guid;
    }

    /**
     * Return the current logged in user, or null if none.
     */
    public static User getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        if ( context != null ) {
            Authentication authentication = context.getAuthentication();
            if ( authentication != null )
                return (User) authentication.getPrincipal();
        }

        return null;
    }

    /**
     * Return the created.
     */
    public Date getCreated() {
        return this.created;
    }

    /**
     * Return the creator.
     */
    public User getCreator() {
        return this.creator;
    }

    /**
     * Return the guid.
     */
    public GUID getGuid() {
        return this.guid;
    }

    /**
     * Return the lastModified.
     */
    public Date getLastModified() {
        return this.lastModified;
    }

    /**
     * Set the lastModified.
     * @param lastModified the lastModified
     */
    public void setLastModified( Date lastModified ) {
        this.lastModified = lastModified;
    }

    /**
     * Return the lastModifier.
     */
    public User getLastModifier() {
        return this.lastModifier;
    }

    /**
     * Set the lastModifier.
     * @param lastModifier the lastModifier
     */
    public void setLastModifier( User lastModifier ) {
        this.lastModifier = lastModifier;
    }

    /**
     * Overriden from ....
     * @see Element#hasAuthority(User)
     * @param user the user
     */
    public boolean hasAuthority( User user ) {
        // TODO Auto-generated method stub
        return true;
    }
}
