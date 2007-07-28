// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import java.util.List;

import com.mindalliance.channels.data.definitions.Policy;
import com.mindalliance.channels.data.definitions.Situation;
import com.mindalliance.channels.data.profiles.Resource;
import com.mindalliance.channels.data.support.GUID;

/**
 * A realized environment, triggered by an event and in effect for the
 * duration of the event. For the duration of the circumstance, the
 * operationality of some resources and the activation of some
 * policies differ from the default case. Circumstances can be composed
 * (commutatively) when they co-occur to determine what the resulting
 * resource and policy environment is.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Circumstance extends Occurrence {

    /**
     * The realized environment, set for "atomic" circumstances.
     */
    private Situation environment;

    /** Set for "atomic" circumstances. */
    private Event trigger;

    /** Set for "composed" circumstances. */
    private List<Circumstance> components;

    /**
     * Default constructor.
     */
    public Circumstance() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Circumstance( GUID guid ) {
        super( guid );
    }

    /**
     * Compose with another circumstance.
     * @param circumstance the other circumstance
     */
    public Circumstance composeWith( Circumstance circumstance ) {
        return null;
    }

    /**
     * Test if a resource is operational.
     * @param resource the resource
     */
    public boolean isResourceOperational( Resource resource ) {
        return false;
    }

    /**
     * Test if given policy is in effect.
     * @param policy the policy
     */
    public boolean isPolicyInEffect( Policy policy ) {
        return false;
    }

    /**
     * Return the components.
     */
    public List<Circumstance> getComponents() {
        return components;
    }

    /**
     * Set the components.
     * @param components the components to set
     */
    public void setComponents( List<Circumstance> components ) {
        this.components = components;
    }

    /**
     * Return the environment.
     */
    public Situation getEnvironment() {
        return environment;
    }

    /**
     * Set the environment.
     * @param environment the environment to set
     */
    public void setEnvironment( Situation environment ) {
        this.environment = environment;
    }

    /**
     * Return the trigger.
     */
    public Event getTrigger() {
        return trigger;
    }

    /**
     * Set the trigger.
     * @param trigger the trigger to set
     */
    public void setTrigger( Event trigger ) {
        this.trigger = trigger;
    }

}
