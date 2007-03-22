// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.reference.Type;

/**
 * A task carried out by one or more agents in response to an event
 * in order to fulfill role-based responsibilities. A task can
 * have outcomes.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 * @composed - - * Outcome
 */
public class Task extends Occurence {

    private List<Outcome> outcomes = new ArrayList<Outcome>();
    private List<Type> responsibilities = new ArrayList<Type>();

    /**
     * Default constructor.
     */
    Task() {
        super();
    }

    /**
     * Convenience constructor.
     * @param scenario the scenario
     */
    public Task( Scenario scenario ) {
        super( scenario );
    }

    /**
     * Return the value of outcomes.
     */
    public List<Outcome> getOutcomes() {
        return this.outcomes;
    }

    /**
     * Set the value of outcomes.
     * @param outcomes The new value of outcomes
     */
    public void setOutcomes( List<Outcome> outcomes ) {
        this.outcomes = outcomes;
    }

    /**
     * Return the value of responsibilities.
     */
    public List<Type> getResponsibilities() {
        return this.responsibilities;
    }

    /**
     * Set the value of responsibilities.
     * @param responsibilities The new value of responsibilities
     */
    public void setResponsibilities( List<Type> responsibilities ) {
        this.responsibilities = responsibilities;
    }

    /**
     * Add a responsibility.
     * @param responsibility the responsibility
     */
    public void addResponsibility( Type responsibility ) {
        this.responsibilities.add( responsibility );
    }

    /**
     * Remove a responsibility.
     * @param responsibility the responsibility
     */
    public void removeResponsibility( Type responsibility ) {
        this.responsibilities.remove( responsibility );
    }
}
