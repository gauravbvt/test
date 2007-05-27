/*
 * Created on Apr 30, 2007
 */
package com.mindalliance.channels.data.elements.assertions;

import com.mindalliance.channels.data.OptOutable;
import com.mindalliance.channels.data.elements.resources.Actor;
import com.mindalliance.channels.data.elements.scenario.Environment;
import com.mindalliance.channels.util.GUID;

/**
 * Assertion on agents that excludes a role, a person or a team from
 * being implied by an agent.
 * 
 * @author jf
 */
public class OptedOut extends Assertion {

    private Actor optingOut; // who is opting out
    private Environment environment; // in what environment only
                                        // (in all if null)

    public OptedOut() {
        super();
    }

    public OptedOut( GUID guid ) {
        super( guid );
    }

    public OptOutable getOptOutable() {
        return (OptOutable) getAbout();
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
     * @return the optingOut
     */
    public Actor getOptingOut() {
        return optingOut;
    }

    /**
     * @param optingOut the optingOut to set
     */
    public void setOptingOut( Actor optingOut ) {
        this.optingOut = optingOut;
    }

}
