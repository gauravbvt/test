/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import com.mindalliance.channels.data.Actor;
import com.mindalliance.channels.data.Knowable;
import com.mindalliance.channels.data.Level;
import com.mindalliance.channels.data.Needy;
import com.mindalliance.channels.data.ScenarioElement;
import com.mindalliance.channels.util.Duration;

/**
 * Assertion made about someone needing information, possibly about a scenario element.
 * @author jf
 *
 */
public class NeedsToKnow extends Assertion {

	private Information information; // what information
	private ScenarioElement subject; // about what scenario element if any (situational awareness need)
	private Level criticality; // How critical is this information
	private Duration window; // After which knowing is of no value (if an agent needs to know, the window should not exceed the duration of the task)

	public Needy getNeedy() {
		return (Needy)getAbout();
	}

}
