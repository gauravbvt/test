/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.user;

import com.mindalliance.channels.data.support.Level;
import com.mindalliance.channels.util.GUID;

/**
 * A more or less urgent announcement to selected users, as requested, of an application event.
 * @author jf
 *
 */
public class Alert extends Announcement  {
	
	private Level Priority;
	private NotificationRequest notificationRequest;
	
	public Alert() {
		super();
	}
	public Alert(GUID guid) {
		super(guid);
	}
	/**
	 * @return the notificationRequest
	 */
	public NotificationRequest getNotificationRequest() {
		return notificationRequest;
	}
	/**
	 * @param notificationRequest the notificationRequest to set
	 */
	public void setNotificationRequest(NotificationRequest notificationRequest) {
		this.notificationRequest = notificationRequest;
	}
	/**
	 * @return the priority
	 */
	public Level getPriority() {
		return Priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Level priority) {
		Priority = priority;
	}

}
