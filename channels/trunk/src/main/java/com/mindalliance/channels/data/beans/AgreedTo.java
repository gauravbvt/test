/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import com.mindalliance.channels.data.Agreeable;
import com.mindalliance.channels.data.elements.Organization;

/**
 * 
 * @author jf
 * Assertion that an exchange etc. has be agreed to by the organization
 * who is the source of the information shared.
 */
public class AgreedTo extends Assertion {
	
	private Organization organization;
	private Environment environment; // Only in this environment (in all if null)
	
	public Agreeable getAgreeable() {
		return (Agreeable)getAbout();
	}

}
