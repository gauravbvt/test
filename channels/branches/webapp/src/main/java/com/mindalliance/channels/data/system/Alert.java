// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import com.mindalliance.channels.data.support.GUID;
import com.mindalliance.channels.data.support.Level;

/**
 * A more or less urgent announcement to selected users, as requested,
 * of an application event.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 *
 * @composed - - 1 NotificationRequest
 */
public class Alert extends Announcement {

    private Level priority;
    private NotificationRequest notificationRequest;

    /**
     * Default constructor.
     */
    public Alert() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Alert( GUID guid ) {
        super( guid );
    }

    /**
     * Return the notification request.
     */
    public NotificationRequest getNotificationRequest() {
        return notificationRequest;
    }

    /**
     * Set the notification request.
     * @param notificationRequest the notification request
     */
    public void setNotificationRequest(
            NotificationRequest notificationRequest ) {

        this.notificationRequest = notificationRequest;
    }

    /**
     * Return the priority.
     */
    public Level getPriority() {
        return this.priority;
    }

    /**
     * Set the priority.
     * @param priority the priority to set
     */
    public void setPriority( Level priority ) {
        this.priority = priority;
    }

}
