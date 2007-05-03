/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.Caused;
import com.mindalliance.channels.data.components.Cause;
import com.mindalliance.channels.data.support.Level;
import com.mindalliance.channels.util.Duration;

/**
 * An event in a scenario that occurs possibly with some delay. The event may be caused by a task
 * or not (then an incident "caused" implicitly by the start of the scenario it's in).
 * An event may be terminated by any of one or more tasks, or it may terminate on its own after some time.
 * @author jf
 *
 */
public class Event extends AbstractOccurrence implements Caused {

	private Level probability; // LOW, MEDIUM or HIGH
	private Cause cause; // Set if a task or event causes it (after some delay from starting or ending)
	private List<Task> terminatedBy; // Set if a task terminates it
	private Duration duration; // Set if event is self-terminating

	/** 
	 * Get cause
	 */
	public Cause getCause() {
		return null;
	}
	
}
