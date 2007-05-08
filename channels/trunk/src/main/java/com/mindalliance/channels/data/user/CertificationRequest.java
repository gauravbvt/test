/*
 * Created on May 3, 2007
 *
 */
package com.mindalliance.channels.data.user;

import com.mindalliance.channels.util.GUID;

/**
 * Request for certification by an applicable authority
 * @author jf
 *
 */
public class CertificationRequest extends UserRequest {
	
	enum Kind {IDENTIY, ROLE};
	
	private GUID target; // GUID of either UserProfile or Person

}
