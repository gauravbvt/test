/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.components;

import com.mindalliance.channels.data.Occurrence;
import com.mindalliance.channels.util.AbstractJavaBean;
import com.mindalliance.channels.data.support.Duration;

/**
 * The cause of an occurrence which is either the start or end of an occurrence.
 * The creation of the effect may be delayed (e.g. event begins 2 minutes after the task starts)
 * @author jf
 *
 */
abstract public class Cause<T extends Occurrence> extends AbstractJavaBean {

	private boolean isStart; // Is the cause the start of the occurrence or the end?
	private Duration delay; // How long after the start or end of the occurrence before the event begins
	/**
	 * Get the occurrence that caused this.
	 * @return
	 */
	abstract public T getOccurrence();

	/**
	 * @return the delay
	 */
	public Duration getDelay() {
		return delay;
	}

	/**
	 * @param delay the delay to set
	 */
	public void setDelay(Duration delay) {
		this.delay = delay;
	}

	/**
	 * @return the isStart
	 */
	public boolean isStart() {
		return isStart;
	}

	/**
	 * @param isStart the isStart to set
	 */
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	
}
