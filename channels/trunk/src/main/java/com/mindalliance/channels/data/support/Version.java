/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.mindalliance.channels.data.elements.Element;
import com.mindalliance.channels.data.elements.UserProfile;
import com.mindalliance.channels.data.user.Opinion;

// The version of an Element including its then state (frozen, thawed, deleted)
public class Version implements Serializable {
	
	private Element clone; // version of the element per se, unless deleted or frozen
	private Date when;
	private UserProfile who;
	private boolean deleted = false;
	private boolean frozen = false;
	private List<Opinion> opinions;

}
