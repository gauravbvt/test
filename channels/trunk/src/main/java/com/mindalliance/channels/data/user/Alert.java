/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.user;

import com.mindalliance.channels.data.support.Level;

/**
 * A more or less urgent announcement to selected users, as requested, of an application event.
 * @author jf
 *
 */
public class Alert extends Announcement  {
	
	private Level Priority;
	private NotificationRequest notificationRequest;

}
