// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.user;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.mindalliance.channels.User;

/**
 * The version of an object including its then state (frozen, thawed,
 * deleted).
 *
 * @param <T> the type of the versioned object
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Version<T extends Serializable> implements Serializable {

    /** Version of the element per se, unless deleted or frozen. */
    private T clone;
    private Date when;
    private User who;
    private boolean deleted;
    private boolean frozen;
    private List<Opinion> opinions;

    /**
     * Default constructor.
     */
    public Version() {
    }

    /**
     * Return the clone.
     */
    public T getClone() {
        return clone;
    }

    /**
     * Set the clone.
     * @param clone the clone to set
     */
    public void setClone( T clone ) {
        this.clone = clone;
    }

    /**
     * Return the deletion status.
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Set the deletion status.
     * @param deleted the deleted to set
     */
    public void setDeleted( boolean deleted ) {
        this.deleted = deleted;
    }

    /**
     * Return the freeze status.
     */
    public boolean isFrozen() {
        return frozen;
    }

    /**
     * Set the freeze status.
     * @param frozen the frozen to set
     */
    public void setFrozen( boolean frozen ) {
        this.frozen = frozen;
    }

    /**
     * Return the opinions attached to this version.
     */
    public List<Opinion> getOpinions() {
        return opinions;
    }

    /**
     * Set the opinions attached to this version.
     * @param opinions the opinions to set
     */
    public void setOpinions( List<Opinion> opinions ) {
        this.opinions = opinions;
    }

    /**
     * Add an opinion.
     * @param opinion the opinion
     */
    public void addOpinion( Opinion opinion ) {
        opinions.add( opinion );
    }

    /**
     * Remove an opinion.
     * @param opinion the opinion
     */
    public void removeOpinion( Opinion opinion ) {
        opinions.remove( opinion );
    }

    /**
     * Return the date this version was created.
     */
    public Date getWhen() {
        return when;
    }

    /**
     * Set the date this version was created.
     * @param when the when to set
     */
    public void setWhen( Date when ) {
        this.when = when;
    }

    /**
     * Return the user who created this version.
     */
    public User getWho() {
        return who;
    }

    /**
     * Set the user who created this version.
     * @param who the user to set
     */
    public void setWho( User who ) {
        this.who = who;
    }

}
