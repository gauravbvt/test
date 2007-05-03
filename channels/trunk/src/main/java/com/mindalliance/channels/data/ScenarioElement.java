/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.data;

import com.mindalliance.channels.data.elements.Scenario;

/**
 * An element that exists only in the context of a scenario.
 * @author jf
 *
 */
public interface ScenarioElement extends Element, Describable {
	
	/**
	 * 
	 * @return the scenario this element is in
	 */
	Scenario getScenario();

}
