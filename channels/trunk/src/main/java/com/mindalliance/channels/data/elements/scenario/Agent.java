/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.Actor;
import com.mindalliance.channels.data.Connected;
import com.mindalliance.channels.data.Knowledgeable;
import com.mindalliance.channels.data.Needy;
import com.mindalliance.channels.data.OptOutable;
import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.elements.assertions.Assertion;
import com.mindalliance.channels.data.elements.assertions.CanAccess;
import com.mindalliance.channels.data.elements.assertions.NeedsToKnow;
import com.mindalliance.channels.data.elements.assertions.OptedOut;
import com.mindalliance.channels.data.elements.project.Project;
import com.mindalliance.channels.data.elements.resources.Role;
import com.mindalliance.channels.util.GUID;


/**
 * A specification of who is executing a task, together or separately.
 * @author jf
 *
 */
abstract public class Agent extends AbstractScenarioElement implements Actor, Needy, Connected, OptOutable, Knowledgeable {
	
	private Task task;
		
	public Agent() {
		super();
	}

	public Agent(GUID guid) {
		super(guid);
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Actor#getRoles()
	 */
	abstract public List<Role> getRoles(Project project);

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Connected#getCanAccessAssertions()
	 */
	public List<CanAccess> getCanAccessAssertions() {
		List<CanAccess> canAccessAssertions = new ArrayList<CanAccess>();
		for (Assertion assertion : getAssertions()) {
			if (assertion instanceof CanAccess)
				canAccessAssertions.add((CanAccess)assertion);
		}
		return canAccessAssertions;
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Needy#getNeedsToKnowAssertions()
	 */
	public List<NeedsToKnow> getNeedsToKnowAssertions() {
		List<NeedsToKnow> needsToKnowAssertions = new ArrayList<NeedsToKnow>();
		for (Assertion assertion : getAssertions()) {
			if (assertion instanceof NeedsToKnow)
				needsToKnowAssertions.add((NeedsToKnow)assertion);
		}
		return needsToKnowAssertions;
	}
	
	/**
	 * @return the optedOutAssertions
	 */
	public List<OptedOut> getOptedOutAssertions() {
		List<OptedOut> optedOutAssertions = new ArrayList<OptedOut>();
		for (Assertion assertion : getAssertions()) {
			if (assertion instanceof OptedOut)
				optedOutAssertions.add((OptedOut)assertion);
		}
		return optedOutAssertions;
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Knowledgeable#knows(com.mindalliance.channels.data.beans.Information)
	 */
	public boolean knows(Information information) {
		return false; // TODO
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Knowledgeable#needsToKnow(com.mindalliance.channels.data.beans.Information)
	 */
	public boolean needsToKnow(Information information) {
		return false; // TODO
	}

	/**
	 * @return the task
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(Task task) {
		this.task = task;
	}


}
