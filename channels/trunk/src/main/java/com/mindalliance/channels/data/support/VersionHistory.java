/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.mindalliance.channels.data.elements.UserProfile;
import com.mindalliance.channels.util.GUID;

/** 
 * The change history of an element.
 * @author jf
 *
 */
public class VersionHistory implements Serializable {

	private GUID guid; // guid of element
	private List<Version> priorVersions;
	
	public boolean isDeleted() {
		return false;
	}

	public boolean isFrozen() {
		return false;
	}
	
	public Date whenLastChanged() {
		return null;
	}
	
	public Date whenCreated() {
		return null;
	}
	
	public List<UserProfile> getContributors() {
		return null;
	}
}
