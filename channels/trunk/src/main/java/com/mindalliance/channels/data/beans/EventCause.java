/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import com.mindalliance.channels.data.elements.Event;

/**
 * Event as cause
 * @author jf
 *
 */
public class EventCause extends Cause {
	
	private Event event;
	
	public Event getOccurrence() {
		return event;
	}

	@Override
	public boolean isCausedByEvent() {
		return true;
	}

	@Override
	public boolean isCausedByTask() {
		return false;
	}

}
