// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.util.Date;
import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.definitions.CategorySet;
import com.mindalliance.channels.definitions.Category.Taxonomy;
import com.mindalliance.channels.profiles.InferableObject;
import com.mindalliance.channels.support.Element;
import com.mindalliance.channels.support.GUID;

/**
 * Some announcement about an element and targeting one or more users.
 * An announcement know by what user it has been acknowledged and/or
 * dismissed.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class Announcement extends InferableObject {

    /** Who this announcement is meant for. */
    private CategorySet audience = new CategorySet( Taxonomy.Role );

    private Date timestamp =
        new Date( java.lang.System.currentTimeMillis() );

    /** Null if system generated. */
    private User createdBy;
    private Element about;

    /** Users who have acknowledged the announcement. */
    private List<User> acknowledgers;

    /** Users who have dismissed the announcement. */
    private List<User> dismissers;

    /**
     * Default constructor.
     */
    public Announcement() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Announcement( GUID guid ) {
        super( guid );
    }

    /**
     * Return the element referred to by this announcement.
     */
    public Element getAbout() {
        return about;
    }

    /**
     * Set the element referred to by this announcement.
     * @param about the about to set
     */
    public void setAbout( Element about ) {
        this.about = about;
    }

    /**
     * Return the audience.
     */
    public CategorySet getAudience() {
        return audience;
    }

    /**
     * Set the audience.
     * @param audience the audience to set
     */
    public void setAudience( CategorySet audience ) {
        this.audience = audience;
    }

    /**
     * Return the user who created this announcement.
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the user who created this announcement.
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy( User createdBy ) {
        this.createdBy = createdBy;
    }

    /**
     * Return the dismissers.
     */
    public List<User> getDismissers() {
        return dismissers;
    }

    /**
     * Set the dismissers.
     * @param dismissers the dismissers to set
     */
    public void setDismissers( List<User> dismissers ) {
        this.dismissers = dismissers;
    }

    /**
     * Add a dismisser.
     * @param user the dismisser
     */
    public void addDismisser( User user ) {
        dismissers.add( user );
    }

    /**
     * Remove a dismisser.
     * @param user the dismisser
     */
    public void removeDismisser( User user ) {
        dismissers.remove( user );
    }

    /**
     * Return the timestamp.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Set the timestamp.
     * @param timestamp the timestamp to set
     */
    public void setTimestamp( Date timestamp ) {
        this.timestamp = timestamp;
    }

    /**
     * Return the acknowledgers.
     */
    public List<User> getAcknowledgers() {
        return acknowledgers;
    }

    /**
     * Set the acknowledgers.
     * @param acknowledgers the acknowledgers to set
     */
    public void setAcknowledgers( List<User> acknowledgers ) {
        this.acknowledgers = acknowledgers;
    }

    /**
     * Add an acknowledger.
     * @param user the acknowledger
     */
    public void addAcknowledger( User user ) {
        acknowledgers.add( user );
    }

    /**
     * Add an acknowledger.
     * @param user the acknowledger
     */
    public void removeAcknowledger( User user ) {
        acknowledgers.remove( user );
    }
}
