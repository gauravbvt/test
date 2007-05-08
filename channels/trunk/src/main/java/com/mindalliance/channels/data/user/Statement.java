/*
 * Created on May 3, 2007
 *
 */
package com.mindalliance.channels.data.user;

import java.io.Serializable;
import java.util.Date;

import com.mindalliance.channels.data.Unique;
import com.mindalliance.channels.data.elements.UserProfile;
import com.mindalliance.channels.util.GUID;

/**
 * A statement made by a user
 * @author jf
 *
 */
public abstract class Statement implements Serializable, Unique {

	private GUID guid;
	private UserProfile user;
	private Date when;
	private String content;
	/**
	 * @return the guid
	 */
	public GUID getGuid() {
		return guid;
	}
}
