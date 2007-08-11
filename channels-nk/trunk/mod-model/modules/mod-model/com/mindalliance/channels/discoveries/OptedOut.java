// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.discoveries;

import com.mindalliance.channels.definitions.Situation;
import com.mindalliance.channels.models.Assertion;
import com.mindalliance.channels.profiles.Actor;
import com.mindalliance.channels.support.GUID;

/**
 * Assertion on agents that excludes a role, a person or a team from
 * being implied by an agent.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class OptedOut extends Assertion {

    private Actor optingOut;
    private Situation environment;

    /**
     * Default constructor.
     */
    public OptedOut() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public OptedOut( GUID guid ) {
        super( guid );
    }

    /**
     * Return the environment.
     */
    public Situation getEnvironment() {
        return environment;
    }

    /**
     * Set the environment.
     * @param environment the environment
     */
    public void setEnvironment( Situation environment ) {
        this.environment = environment;
    }

    /**
     * Return the optingOut actor.
     */
    public Actor getOptingOut() {
        return optingOut;
    }

    /**
     * Set the optingOut actor.
     * @param optingOut the optingOut
     */
    public void setOptingOut( Actor optingOut ) {
        this.optingOut = optingOut;
    }
}
