/*
 * Created on May 7, 2007
 *
 */
package com.mindalliance.channels.data;

import com.mindalliance.channels.data.support.Duration;

/**
 * Something that happens at some point after "time zero"
 * @author jf
 *
 */
public interface Timed {
	/**
	 * Gets the duration
	 * @return Duration
	 */
	public Duration getDuration();
	
	/**
	 * Returns the delta from the scenario time zero.
	 * @return Duration
	 */
	public Duration getTime();
	
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
