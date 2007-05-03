/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import com.mindalliance.channels.data.Actor;
import com.mindalliance.channels.data.Knowable;

/**
 * Assertion made about some scenario element known of by someone.
 * @author jf
 *
 */
public class Known extends Assertion {

	private Information information; // what's known
	private Actor actor; // know by whom: an agent ("whoever does this task knows this"), role ("anyone in this role knows this") or team who knows this

	public Knowable getKnowable() {
		return (Knowable)getAbout();
	}

	
}
