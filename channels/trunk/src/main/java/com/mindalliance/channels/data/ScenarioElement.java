/*
 * Created on Apr 28, 2007
 */
package com.mindalliance.channels.data;

import com.mindalliance.channels.data.elements.project.Scenario;

/**
 * An element that exists only in the context of a scenario. All
 * scenario elements are knowable.
 * 
 * @author jf
 */
public interface ScenarioElement extends Element, Describable, Knowable {

    /**
     * @return the scenario this element is in
     */
    Scenario getScenario();

}
