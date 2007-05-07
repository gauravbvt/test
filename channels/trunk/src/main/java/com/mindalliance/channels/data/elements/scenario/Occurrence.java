/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.data.Caused;
import com.mindalliance.channels.data.Located;
import com.mindalliance.channels.data.Timed;
import com.mindalliance.channels.data.support.Duration;

/**
 * Something in a scenario that can happen somewhere and after, before or during some other occurrence, or after "time zero".
 * An occurrence may have a known cause.
 * @author jf
 *
 */
public interface Occurrence extends ScenarioElement, Timed, Located, Caused {
	
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
