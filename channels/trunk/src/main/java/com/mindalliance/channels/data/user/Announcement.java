/*
 * Created on May 2, 2007
 */
package com.mindalliance.channels.data.user;

import java.util.Date;
import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.support.Pattern;
import com.mindalliance.channels.util.GUID;

/**
 * Some announcement about an element and targeting one or more users.
 * An announcement know by what user it has been acknowledged and/or
 * dismissed.
 * 
 * @author jf
 */
public abstract class Announcement extends AbstractElement {

    private Pattern<User> audience; // Who this announcement is meant
                                    // for
    private Date timestamp;
    private User createdBy; // null if system
    private Element about;
    private List<User> acknowledgers; // Users who have acknowledged
                                        // the announcement
    private List<User> dismissers; // Users who have dismissed the
                                    // announcement

    public Announcement() {
        super();
    }

    public Announcement( GUID guid ) {
        super( guid );
    }

    /**
     * @return the about
     */
    public Element getAbout() {
        return about;
    }

    /**
     * @param about the about to set
     */
    public void setAbout( Element about ) {
        this.about = about;
    }

    /**
     * @return the audience
     */
    public Pattern<User> getAudience() {
        return audience;
    }

    /**
     * @param audience the audience to set
     */
    public void setAudience( Pattern<User> audience ) {
        this.audience = audience;
    }

    /**
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy( User createdBy ) {
        this.createdBy = createdBy;
    }

    /**
     * @return the dismissers
     */
    public List<User> getDismissers() {
        return dismissers;
    }

    /**
     * @param dismissers the dismissers to set
     */
    public void setDismissers( List<User> dismissers ) {
        this.dismissers = dismissers;
    }

    /**
     * @param user
     */
    public void addDismisser( User user ) {
        dismissers.add( user );
    }

    /**
     * @param user
     */
    public void removeDismisser( User user ) {
        dismissers.remove( user );
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp( Date timestamp ) {
        this.timestamp = timestamp;
    }

    /**
     * @return the acknowledgers
     */
    public List<User> getAcknowledgers() {
        return acknowledgers;
    }

    /**
     * @param acknowledgers the acknowledgers to set
     */
    public void setAcknowledgers( List<User> acknowledgers ) {
        this.acknowledgers = acknowledgers;
    }

    /**
     * @param user
     */
    public void addAcknowledger( User user ) {
        acknowledgers.add( user );
    }

    /**
     * @param user
     */
    public void removeAcknowledger( User user ) {
        acknowledgers.remove( user );
    }
}
