/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.Caused;
import com.mindalliance.channels.data.Excludable;
import com.mindalliance.channels.data.elements.assertions.Assertion;
import com.mindalliance.channels.data.elements.assertions.CanAccess;
import com.mindalliance.channels.data.elements.assertions.Excluded;
import com.mindalliance.channels.data.support.Level;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.channels.util.GUID;

/**
 * An event in a scenario that occurs possibly with some delay. The event may be caused by a task
 * or not (then an incident "caused" implicitly by the start of the scenario it's in).
 * An event may be terminated by any of one or more tasks, or it may terminate on its own after some time.
 * @author jf
 *
 */
public class Event extends AbstractOccurrence implements Caused, Excludable {

	private Level probability; // LOW, MEDIUM or HIGH
	private List<Task> terminatingTasks; // Set if a task terminates it
	private Duration duration; // Set if event is self-terminating
	
	public Event() {
		super();
	}

	public Event(GUID guid) {
		super(guid);
	}

	public List<Excluded> getExcludedAssertions() {
		List<Excluded> excludedAssertions = new ArrayList<Excluded>();
		for (Assertion assertion : getAssertions()) {
			if (assertion instanceof Excluded)
				excludedAssertions.add((Excluded)assertion);
		}
		return excludedAssertions;
	}

	/**
	 * @return the duration
	 */
	public Duration getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	/**
	 * @return the probability
	 */
	public Level getProbability() {
		return probability;
	}

	/**
	 * @param probability the probability to set
	 */
	public void setProbability(Level probability) {
		this.probability = probability;
	}

	/**
	 * @return the terminatingTasks
	 */
	public List<Task> getTerminatingTasks() {
		return terminatingTasks;
	}

	/**
	 * @param terminatingTasks the terminatingTasks to set
	 */
	public void setTerminatingTasks(List<Task> terminatingTasks) {
		this.terminatingTasks = terminatingTasks;
	}
	/**
	 * 
	 * @param task
	 */
	public void addTerminatingTask(Task task) {
		terminatingTasks.add(task);
	}
	/**
	 * 
	 * @param task
	 */
	public void removeTerminatingTask(Task task) {
		terminatingTasks.remove(task);
	}
	
}
