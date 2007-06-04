// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.scenario;

import java.util.ArrayList;
import java.util.List;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.ScenarioElement;
import com.mindalliance.channels.data.elements.assertions.Assertion;
import com.mindalliance.channels.data.elements.assertions.Known;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.data.reference.Information;
import com.mindalliance.channels.util.GUID;

/**
 * An element in a scenario.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class AbstractScenarioElement extends AbstractElement
    implements ScenarioElement {

    /** Backpointer... */
    private Scenario scenario;
    private Information descriptor;

    /**
     * Default constructor.
     */
    public AbstractScenarioElement() {
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public AbstractScenarioElement( GUID guid ) {
        super( guid );
    }

    /**
     * Get the known assertions.
     */
    @PropertyOptions(ignore=true)
    public List<Known> getKnownAssertions() {
        List<Known> list = new ArrayList<Known>();
        for ( Assertion assertion : getAssertions() )
            if ( assertion instanceof Known )
                list.add( (Known) assertion );
        return list;
    }

    /**
     * Return the descriptor.
     */
    @PropertyOptions(ignore=true)
    public Information getDescriptor() {
        return this.descriptor;
    }

    /**
     * Set the value of descriptor.
     * @param descriptor The new value of descriptor
     */
    @PropertyOptions(ignore=true)
    public void setDescriptor( Information descriptor ) {
        this.descriptor = descriptor;
    }

    /**
     * Return the scenario.
     */
    @PropertyOptions(ignore=true)
    public Scenario getScenario() {
        return scenario;
    }

    /**
     * Set the scenario.
     * @param scenario the scenario to set
     */
    public void setScenario( Scenario scenario ) {
        this.scenario = scenario;
    }
}
