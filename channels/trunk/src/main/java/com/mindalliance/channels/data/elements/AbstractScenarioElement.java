/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import com.mindalliance.channels.data.ScenarioElement;
import com.mindalliance.channels.data.beans.Information;

/**
 * An element in a scenario.
 * @author jf
 *
 */
abstract public class AbstractScenarioElement extends AbstractElement implements ScenarioElement {
	
	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Describable#getDescriptor()
	 */
	public Information getDescriptor() {
		return null;
	}

	private Scenario scenario;

	/**
	 * @return the scenario
	 */
	public Scenario getScenario() {
		return scenario;
	}

	/**
	 * @param scenario the scenario to set
	 */
	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}


}
