/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.assertions;

import com.mindalliance.channels.data.elements.reference.Environment;
import com.mindalliance.channels.data.elements.reference.Policy;

/**
 * Assertion that some occurrence is regulated by a policy.
 * @author jf
 *
 */
public class Regulated extends Assertion {
	
	/**
	 * Policy that applies
	 */
	private Policy policy; // by what policy
	private Environment environment; // only in this environment (in any if null)
	private boolean forbids; // policy forbids else obligates
	
	public Regulatable getRegulatable() {
		return (Regulatable)getAbout();
	}

}
