/*
 * Created on May 7, 2007
 *
 */
package com.mindalliance.channels.data.elements.assertions;

/**
 * Assertion that something is mutually exclusive with something else.
 * @author jf
 *
 */
public class Excluded extends Assertion {
	
	private Excludable mutuallyExcluded;
	
	public Excludable getExcludable() {
		return (Excludable)getAbout();
	}

}
