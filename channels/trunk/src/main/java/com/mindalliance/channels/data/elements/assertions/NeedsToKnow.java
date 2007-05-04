/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.assertions;

import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.elements.scenario.ScenarioElement;
import com.mindalliance.channels.data.support.Level;
import com.mindalliance.channels.util.Duration;

/**
 * Assertion made about someone needing information, possibly about a scenario element,
 * to be delivered via notification or by responding to a request.
 * @author jf
 *
 */
public class NeedsToKnow extends Assertion {
	
	enum Kind {NOTIFICATION, REQUEST_RESPONSE};

	private Kind method;
	private Information information; // what information
	private ScenarioElement subject; // about what scenario element if any (situational awareness need)
	private Level criticality; // How critical is this information
	private Duration window; // After which knowing is of no value (if an agent needs to know, the window should not exceed the duration of the task)

	public Needy getNeedy() {
		return (Needy)getAbout();
	}

}
