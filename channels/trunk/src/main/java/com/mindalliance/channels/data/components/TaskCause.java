/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.components;

import com.mindalliance.channels.data.elements.scenario.Task;

/**
 * Task initiation or completion success or failure as cause.
 * @author jf
 *
 */
public class TaskCause extends Cause {

	private Task task;
	private boolean success;
	
	public Task getOccurrence() {
		return task;
	}

	@Override
	public boolean isCausedByEvent() {
		return false;
	}

	@Override
	public boolean isCausedByTask() {
		return true;
	}

}
