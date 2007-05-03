/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.beans.Pattern;
import com.mindalliance.channels.data.beans.UserProfile;

/**
 * Some actionable information targeting one or more users.
 * @author jf
 *
 */
public abstract class Attribution extends AbstractElement {
	
	private Pattern<UserProfile> target;

}
