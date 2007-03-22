// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import com.mindalliance.channels.system.Role;

/**
 * An assertion about an agent's relationship to an element of
 * the scenario that *could* become true at some point in time.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @navassoc - who 1 Role
 * @navassoc - what 1 ScenarioElement
 * @navassoc - when 0..1 Occurence
 */
public abstract class Assertion extends ScenarioElement {

    private Role who;
    private ScenarioElement what;
    private Occurence when;

    /**
     * Default constructor.
     */
    public Assertion() {
        super();
    }

    /**
     * Default constructor.
     * @param scenario the scenario
     */
    public Assertion( Scenario scenario ) {
        super( scenario );
    }

    /**
     * Return the value of what.
     */
    public ScenarioElement getWhat() {
        return this.what;
    }

    /**
     * Set the value of what.
     * @param what The new value of what
     */
    public void setWhat( ScenarioElement what ) {
        this.what = what;
    }

    /**
     * Return the value of when.
     */
    public Occurence getWhen() {
        return this.when;
    }

    /**
     * Set the value of when.
     * @param when The new value of when
     */
    public void setWhen( Occurence when ) {
        this.when = when;
    }

    /**
     * Return the value of who.
     */
    public Role getWho() {
        return this.who;
    }

    /**
     * Set the value of who.
     * @param who The new value of who
     */
    public void setWho( Role who ) {
        this.who = who;
    }
}
