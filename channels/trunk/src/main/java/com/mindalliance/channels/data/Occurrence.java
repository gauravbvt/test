/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.data;

import com.mindalliance.channels.util.Duration;

/**
 * Something in a scenario that can happen somewhere and after, before or during some other occurrence, or after "time zero".
 * @author jf
 *
 */
public interface Occurrence extends ScenarioElement, Located {
	
	public Duration getDuration();
	
	/**
	 * Returns the delta from the scenario time zero.
	 * @return Duration
	 */
	public Duration getStart();
	
	/**
	 * 
	 * @param occurrence
	 * @return whether this occurrence starts after another starts
	 */
	public boolean isAfter(Occurrence occurrence);
	
	/**
	 * 
	 * @param occurrence
	 * @return whether this occurrence starts after another starts
	 */
	public boolean isBefore(Occurrence occurrence);
	
	/**
	 * 
	 * @param occurrence
	 * @return whether this occurrence overlaps another
	 */
	public boolean isDuring(Occurrence occurrence);
}
