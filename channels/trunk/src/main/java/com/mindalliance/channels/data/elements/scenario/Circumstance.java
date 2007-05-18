/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.Resource;
import com.mindalliance.channels.data.reference.Environment;
import com.mindalliance.channels.data.reference.Policy;
import com.mindalliance.channels.util.GUID;

/**
 * A realized environment, triggered by an event and in effect for the
 * duration of the event. For the duration of the circumstance, the
 * operationality of some resources and the activation of some
 * policies differ from the default case. Circumstances can be composed
 * (commutatively) when they co-occur to determine what the resulting
 * resource and policy environment is.
 * 
 * @author jf
 */
public class Circumstance extends AbstractOccurrence {

    /**
     * The realized environment
     */
    private Environment environment; // set for "atomic" circumstances
    private Event trigger; // set for "atomic" circumstances
    private List<Circumstance> components; // set for "composed" circumstances

    public Circumstance() {
        super();
    }

    public Circumstance( GUID guid ) {
        super( guid );
    }

    public Circumstance composeWith( Circumstance circumstance ) {
        return null;
    }

    public boolean isResourceOperational( Resource resource ) {
        return false;
    }

    public boolean isPolicyInEffect( Policy policy ) {
        return false;
    }

    /**
     * @return the components
     */
    public List<Circumstance> getComponents() {
        return components;
    }

    /**
     * @param components the components to set
     */
    public void setComponents( List<Circumstance> components ) {
        this.components = components;
    }

    /**
     * @return the environment
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * @param environment the environment to set
     */
    public void setEnvironment( Environment environment ) {
        this.environment = environment;
    }

    /**
     * @return the trigger
     */
    public Event getTrigger() {
        return trigger;
    }

    /**
     * @param trigger the trigger to set
     */
    public void setTrigger( Event trigger ) {
        this.trigger = trigger;
    }

}
