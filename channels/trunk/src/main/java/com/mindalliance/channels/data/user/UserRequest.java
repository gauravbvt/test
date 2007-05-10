/*
 * Created on May 3, 2007
 *
 */
package com.mindalliance.channels.data.user;

/**
 * Request made by a user. A request is either active or inactive.
 * @author jf
 *
 */
public abstract class UserRequest extends Statement {
	
	private boolean active; // whether the request is in effect

	public UserRequest() {
		super();
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

}
