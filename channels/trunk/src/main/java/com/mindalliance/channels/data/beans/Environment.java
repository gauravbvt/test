/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import java.util.List;

import com.mindalliance.channels.data.Resource;
import com.mindalliance.channels.data.elements.Event;

/**
 * A specification for situations; during what event they occur, what resources become (un)available 
 * and what policies (do not) apply.
 * @author jf
 *
 */
public class Environment extends ReferenceData {
	
	private List<Pattern<Event>> triggerConditions;
	private List<Resource> availableResources;
	private List<Resource> unavailableResources;
	private List<Policy> enforcedPolicies;
	private List<Policy> unenforcedPolicies;
	
	/**
	 * @return the availableResources
	 */
	public List<Resource> getAvailableResources() {
		return availableResources;
	}
	/**
	 * @param availableResources the availableResources to set
	 */
	public void setAvailableResources(List<Resource> availableResources) {
		this.availableResources = availableResources;
	}
	/**
	 * @return the enforcedPolicies
	 */
	public List<Policy> getEnforcedPolicies() {
		return enforcedPolicies;
	}
	/**
	 * @param enforcedPolicies the enforcedPolicies to set
	 */
	public void setEnforcedPolicies(List<Policy> enforcedPolicies) {
		this.enforcedPolicies = enforcedPolicies;
	}
	/**
	 * @return the unavailableResources
	 */
	public List<Resource> getUnavailableResources() {
		return unavailableResources;
	}
	/**
	 * @param unavailableResources the unavailableResources to set
	 */
	public void setUnavailableResources(List<Resource> unavailableResources) {
		this.unavailableResources = unavailableResources;
	}
	/**
	 * @return the unenforcedPolicies
	 */
	public List<Policy> getUnenforcedPolicies() {
		return unenforcedPolicies;
	}
	/**
	 * @param unenforcedPolicies the unenforcedPolicies to set
	 */
	public void setUnenforcedPolicies(List<Policy> unenforcedPolicies) {
		this.unenforcedPolicies = unenforcedPolicies;
	}
	/**
	 * @return the triggerConditions
	 */
	public List<Pattern<Event>> getTriggerConditions() {
		return triggerConditions;
	}
	/**
	 * @param triggerConditions the triggerConditions to set
	 */
	public void setTriggerConditions(List<Pattern<Event>> triggerConditions) {
		this.triggerConditions = triggerConditions;
	}
	
}
