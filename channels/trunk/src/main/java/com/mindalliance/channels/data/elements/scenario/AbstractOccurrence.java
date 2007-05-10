/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.data.Occurrence;
import com.mindalliance.channels.data.components.Cause;
import com.mindalliance.channels.data.elements.reference.Location;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.channels.util.GUID;

/**
 * Something that happens in a scenario.
 * @author jf
 *
 */
public abstract class AbstractOccurrence extends AbstractScenarioElement
		implements Occurrence {
	
	private Duration time; // when the occurence begins as a time interval from "time zero"
	private Duration duration;
	private Location location;
	private Cause cause;

	
	public AbstractOccurrence() {
		super();
	}

	public AbstractOccurrence(GUID guid) {
		super(guid);
	}

	/** 
	 * Get cause
	 */
	public Cause getCause() {
		return cause;
	}

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

	/**
	 * @return the time
	 */
	public Duration getTime() {
		return time;
	}

	/**
	 * @param cause the cause to set
	 */
	public void setCause(Cause cause) {
		this.cause = cause;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Duration time) {
		this.time = time;
	}

}
