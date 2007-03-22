// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.system.Role;

/**
 * Assertion that an agent would be granted access to another agent,
 * possibly only when some environment is in effect.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @navassoc - - 1 Role
 * @navassoc - - * Environment
 */
public class CanAccess extends Assertion {

    private Role role;
    private List<Environment> environments = new ArrayList<Environment>();

    /**
     * Default constructor.
     */
    public CanAccess() {
        super();
    }

    /**
     * Default constructor.
     * @param scenario the scenario
     */
    public CanAccess( Scenario scenario ) {
        super( scenario );
    }

    /**
     * Return the value of agent.
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * Set the value of role.
     * @param role The new value of role
     */
    public void setRole( Role role ) {
        this.role = role;
    }

    /**
     * Return the value of environments.
     */
    public List<Environment> getEnvironments() {
        return this.environments;
    }

    /**
     * Set the value of environments.
     * @param environments The new value of environments
     */
    public void setEnvironments( List<Environment> environments ) {
        this.environments = environments;
    }

    /**
     * Add an environment.
     * @param environment the environment
     */
    public void addEnvironment( Environment environment ) {
        this.environments.add( environment );
    }

    /**
     * Remove an environment.
     * @param environment the environment
     */
    public void removeEnvironment( Environment environment ) {
        this.environments.remove( environment );
    }

}
