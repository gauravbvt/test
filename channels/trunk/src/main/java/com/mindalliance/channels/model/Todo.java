// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import com.mindalliance.channels.reference.ActivityType;
import com.mindalliance.channels.util.AbstractJavaBean;
import com.mindalliance.channels.util.Duration;

/**
 * Some activity to be done by a user.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @navassoc - target 1 ModelElement
 */
public class Todo extends AbstractJavaBean {

    /**
     * Priority of a Todo. (Low, Medium or High)
     */
    public enum Priority { Low, Medium, High };

    /**
     * Status of a Todo. (TBD, InProgress or Done)
     */
    public enum Status { TBD, InProgress, Done };

    private Priority priority;
    private String about;
    private ModelElement target;
    private ActivityType activity;
    private Status status;
    private boolean rejected;
    private Duration nag;

    /**
     * Default constructor.
     */
    public Todo() {
        super();
    }

    /**
     * Return the value of about.
     */
    public String getAbout() {
        return this.about;
    }

    /**
     * Set the value of about.
     * @param about The new value of about
     */
    public void setAbout( String about ) {
        this.about = about;
    }

    /**
     * Return the value of activity.
     */
    public ActivityType getActivity() {
        return this.activity;
    }

    /**
     * Set the value of activity.
     * @param activity The new value of activity
     */
    public void setActivity( ActivityType activity ) {
        this.activity = activity;
    }

    /**
     * Return the value of nag.
     */
    public Duration getNag() {
        return this.nag;
    }

    /**
     * Set the value of nag.
     * @param nag The new value of nag
     */
    public void setNag( Duration nag ) {
        this.nag = nag;
    }

    /**
     * Return the value of priority.
     */
    public Priority getPriority() {
        return this.priority;
    }

    /**
     * Set the value of priority.
     * @param priority The new value of priority
     */
    public void setPriority( Priority priority ) {
        this.priority = priority;
    }

    /**
     * Return the value of rejected.
     */
    public boolean isRejected() {
        return this.rejected;
    }

    /**
     * Set the value of rejected.
     * @param rejected The new value of rejected
     */
    public void setRejected( boolean rejected ) {
        this.rejected = rejected;
    }

    /**
     * Return the value of status.
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Set the value of status.
     * @param status The new value of status
     */
    public void setStatus( Status status ) {
        this.status = status;
    }

    /**
     * Return the value of target.
     */
    public ModelElement getTarget() {
        return this.target;
    }

    /**
     * Set the value of target.
     * @param target The new value of target
     */
    public void setTarget( ModelElement target ) {
        this.target = target;
    }
}
