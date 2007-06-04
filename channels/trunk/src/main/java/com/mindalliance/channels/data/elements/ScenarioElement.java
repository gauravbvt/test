// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.Knowable;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.data.reference.Describable;

/**
 * An element that exists only in the context of a scenario. All
 * scenario elements are knowable.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface ScenarioElement extends Element, Describable, Knowable {

    /**
     * Return the scenario this element is in.
     */
    @PropertyOptions(ignore=true)
    Scenario getScenario();

    /**
     * Set the scenario.
     * @param scenario the scenario
     */
    void setScenario( Scenario scenario );
}
