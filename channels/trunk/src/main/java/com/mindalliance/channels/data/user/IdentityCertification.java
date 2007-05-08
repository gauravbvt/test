/*
 * Created on May 8, 2007
 *
 */
package com.mindalliance.channels.data.user;

import com.mindalliance.channels.data.elements.UserProfile;
import com.mindalliance.channels.util.GUID;

/**
 * Identity certification
 * @author jf
 *
 */
public class IdentityCertification extends Certification {

	private UserProfile userProfile; // the profile being certified
	private GUID personGUID; // the certified personification
	private String userName; // the user for this profile
	
}
