/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.assertions.Known;
import com.mindalliance.channels.data.elements.project.Scenario;

/**
 * An element in a scenario.
 * @author jf
 *
 */
abstract public class AbstractScenarioElement extends AbstractElement implements ScenarioElement {
	
	private Scenario scenario;

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Knowable#getKnownAssertions()
	 */
	public List<Known> getKnownAssertions() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.channels.data.Describable#getDescriptor()
	 */
	public Information getDescriptor() {
		return null;
	}

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
