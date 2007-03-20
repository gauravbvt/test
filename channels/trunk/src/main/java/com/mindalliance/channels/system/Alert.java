// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.sql.Timestamp;
import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.User;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * A notification to users of some system or project event.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @navassoc - from 1    User
 * @navassoc - for  1..* User
 */
public class Alert extends AbstractJavaBean {

    /**
     * The priority of an alert.
     */
    public enum Priority { Low, Medium, High };

    private Priority priority;
    private User from;
    private String what;
    private Object about;
    private Timestamp when = new Timestamp( System.currentTimeMillis() );
    private Set<User> forUsers = new TreeSet<User>();
    private boolean read;

    /**
     * Default constructor.
     */
    public Alert() {
    }

    /**
     * Return the value of about.
     */
    public Object getAbout() {
        return this.about;
    }

    /**
     * Set the value of about.
     * @param about The new value of about
     */
    public void setAbout( Object about ) {
        this.about = about;
    }

    /**
     * Return the value of forUsers.
     */
    public Set<User> getForUsers() {
        return this.forUsers;
    }

    /**
     * Set the value of forUsers.
     * @param forUsers The new value of forUsers
     */
    public void setForUsers( Set<User> forUsers ) {
        this.forUsers = forUsers;
    }

    /**
     * Return the value of from.
     */
    public User getFrom() {
        return this.from;
    }

    /**
     * Set the value of from.
     * @param from The new value of from
     */
    public void setFrom( User from ) {
        this.from = from;
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
     * Return the value of read.
     */
    public boolean isRead() {
        return this.read;
    }

    /**
     * Set the value of read.
     * @param read The new value of read
     */
    public void setRead( boolean read ) {
        this.read = read;
    }

    /**
     * Return the value of what.
     */
    public String getWhat() {
        return this.what;
    }

    /**
     * Set the value of what.
     * @param what The new value of what
     */
    public void setWhat( String what ) {
        this.what = what;
    }

    /**
     * Return the value of when.
     */
    public Timestamp getWhen() {
        return this.when;
    }

    /**
     * Set the value of when.
     * @param when The new value of when
     */
    public void setWhen( Timestamp when ) {
        this.when = when;
    }
}
