/*
 * Created on Apr 27, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import com.mindalliance.channels.data.Connected;
import com.mindalliance.channels.data.Contactable;
import com.mindalliance.channels.data.elements.Environment;

/**
 * Assertion that someone has been granted access to someone else.
 * @author jf
 *
 */
public class CanAccess extends Assertion {
	
	private Contactable contact; // access to what
	private Environment environment; // in what environment (null if all environments)
	
	public Connected getConnected() {
		return (Connected)getAbout();
	}

}
