/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.user;

import java.util.Date;

import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.Element;
import com.mindalliance.channels.data.elements.UserProfile;
import com.mindalliance.channels.data.support.Pattern;

/**
 * Some announcement about an element and targeting one or more users.
 * @author jf
 *
 */
public abstract class Announcement extends AbstractElement {
	
	private Pattern<UserProfile> audience;
	private Date timestamp;
	private UserProfile createdBy; // null if system
	private Element about;

}
