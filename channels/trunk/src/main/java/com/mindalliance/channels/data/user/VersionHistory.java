// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.util.GUID;

/**
 * The change history of an element.
 *
 * @param <T> the type of the versioned object
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class VersionHistory<T extends Serializable> implements Serializable {

    private boolean deleted;
    private boolean frozen;
    private Date whenLastChanged;
    private Date whenContributed;
    private GUID elementGUID;
    private List<Version<T>> priorVersions;

    /**
     * Default constructor.
     */
    public VersionHistory() {
    }

    /**
     * Return the element GUID.
     */
    public GUID getElementGUID() {
        return elementGUID;
    }

    /**
     * Set the guid.
     * @param elementGUID the elementGUID to set
     */
    public void setElementGUID( GUID elementGUID ) {
        this.elementGUID = elementGUID;
    }

    /**
     * Return the list of contributors.
     */
    public List<User> getContributors() {
        // TODO
        return new ArrayList<User>();
    }

    /**
     * Return the prior versions.
     */
    public List<Version<T>> getPriorVersions() {
        return priorVersions;
    }

    /**
     * Set the prior versions.
     * @param priorVersions the priorVersions to set
     */
    public void setPriorVersions( List<Version<T>> priorVersions ) {
        this.priorVersions = priorVersions;
    }

    /**
     * Add a version.
     * @param version the version
     */
    public void addPriorVersion( Version<T> version ) {
        priorVersions.add( version );
    }

    /**
     * Remove a version.
     * @param version the version
     */
    public void removePriorVersion( Version<T> version ) {
        priorVersions.remove( version );
    }

    /**
     * Return the value of deleted.
     */
    public boolean isDeleted() {
        return this.deleted;
    }

    /**
     * Set the value of deleted.
     * @param deleted The new value of deleted
     */
    public void setDeleted( boolean deleted ) {
        this.deleted = deleted;
    }

    /**
     * Return the value of frozen.
     */
    public boolean isFrozen() {
        return this.frozen;
    }

    /**
     * Set the value of frozen.
     * @param frozen The new value of frozen
     */
    public void setFrozen( boolean frozen ) {
        this.frozen = frozen;
    }

    /**
     * Return the value of whenContributed.
     */
    public Date getWhenContributed() {
        return this.whenContributed;
    }

    /**
     * Set the value of whenContributed.
     * @param whenContributed The new value of whenContributed
     */
    public void setWhenContributed( Date whenContributed ) {
        this.whenContributed = whenContributed;
    }

    /**
     * Return the value of whenLastChanged.
     */
    public Date getWhenLastChanged() {
        return this.whenLastChanged;
    }

    /**
     * Set the value of whenLastChanged.
     * @param whenLastChanged The new value of whenLastChanged
     */
    public void setWhenLastChanged( Date whenLastChanged ) {
        this.whenLastChanged = whenLastChanged;
    }
}
