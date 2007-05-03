/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import com.mindalliance.channels.data.Occurrence;
import com.mindalliance.channels.util.Duration;

/**
 * Something that happens in a scenario.
 * @author jf
 *
 */
public abstract class AbstractOccurrence extends AbstractScenarioElement
		implements Occurrence {
	
	private Duration duration;
	private Location location;


	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	public Duration getDuration() {
		return duration;
	}

	public Duration getStart() {
		return null;
	}

	public boolean isAfter(Occurrence occurrence) {
		return false;
	}

	public boolean isBefore(Occurrence occurrence) {
		return false;
	}

	public boolean isDuring(Occurrence occurrence) {
		return false;
	}

	public Location getLocation() {
		return location;
	}

}
