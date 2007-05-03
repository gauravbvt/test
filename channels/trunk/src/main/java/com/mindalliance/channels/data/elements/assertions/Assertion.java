/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.assertions;

import com.mindalliance.channels.data.elements.scenario.AbstractScenarioElement;

/**
 * An assertion about a scenario element.
 * @author jf
 *
 */
abstract public class Assertion extends AbstractScenarioElement {
	
	private Assertable about;
	
	public Assertable getAbout() {
		return about;
	}

}
