/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.ScenarioElement;
import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.assertions.Known;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.util.GUID;

/**
 * An element in a scenario.
 * 
 * @author jf
 */
abstract public class AbstractScenarioElement extends AbstractElement implements
        ScenarioElement {

    private Scenario scenario; // backpointer

    public AbstractScenarioElement() {
    }

    public AbstractScenarioElement( GUID guid ) {
        super( guid );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.data.Knowable#getKnownAssertions()
     */
    public List<Known> getKnownAssertions() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
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
    public void setScenario( Scenario scenario ) {
        this.scenario = scenario;
    }

}
