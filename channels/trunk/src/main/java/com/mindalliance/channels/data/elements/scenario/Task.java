/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.Caused;
import com.mindalliance.channels.data.Regulatable;
import com.mindalliance.channels.data.components.Cause;
import com.mindalliance.channels.data.elements.assertions.Assertion;
import com.mindalliance.channels.data.elements.assertions.Excluded;
import com.mindalliance.channels.data.elements.assertions.Regulated;
import com.mindalliance.channels.data.elements.reference.Type;
import com.mindalliance.channels.data.support.TypeSet;
import com.mindalliance.channels.util.GUID;

/**
 * A specification of activities. It is carried out by agents which specify roles or teams. 
 * A task is caused either by an event or a task (sub-task). 
 * It can create knowledge, produce artefacts and cause events and tasks. A Task represents
 * a separate activity for each role or team that matches one of its agents.
  * @author jf
 *
 */
public class Task extends AbstractOccurrence implements Caused, Regulatable {
	
	private List<Agent> agents;
	private TypeSet objectives = new TypeSet(Type.OBJECTIVE);

	public Task() {
		super();
	}

	public Task(GUID guid) {
		super(guid);
	}

	public List<Regulated> getRegulatedAssertions() {
		List<Regulated> regulatedAssertions = new ArrayList<Regulated>();
		for (Assertion assertion : getAssertions()) {
			if (assertion instanceof Regulated)
				regulatedAssertions.add((Regulated)assertion);
		}
		return regulatedAssertions;
	}

	/**
	 * @return the agents
	 */
	public List<Agent> getAgents() {
		return agents;
	}

	/**
	 * @param agents the agents to set
	 */
	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}
	/**
	 * 
	 * @param agent
	 */
	public void addAgent(Agent agent) {
		agents.add(agent);
	}
	/**
	 * 
	 * @param agent
	 */
	public void removeAgent(Agent agent) {
		agents.remove(agent);
	}
	/**
	 * @return the objectives
	 */
	public TypeSet getObjectives() {
		return objectives;
	}

	/**
	 * @param objectives the objectives to set
	 */
	public void setObjectives(TypeSet objectives) {
		this.objectives = objectives;
	}
	

}
