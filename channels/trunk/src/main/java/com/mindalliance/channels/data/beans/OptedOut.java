/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import com.mindalliance.channels.data.Actor;
import com.mindalliance.channels.data.OptOutable;
import com.mindalliance.channels.data.elements.Environment;

/**
 * Assertion on agents that excludes a role, a person or a team from being implied by an agent.
 * @author jf
 *
 */
public class OptedOut extends Assertion {
	
	private Actor optingOut; // who is opting out
	private Environment environment; // in what environment only (in all if null)
	
	public OptOutable getOptOutable() {
		return (OptOutable) getAbout();
	}

}
