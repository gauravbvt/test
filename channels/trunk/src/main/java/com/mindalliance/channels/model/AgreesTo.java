// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Assertion that an agent would agree to be party in a
 * communication, possibly only in certain environments.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 * @navassoc - - * Environment
 * @navassoc - - 1 Communication
 */
public class AgreesTo extends Assertion {

    private Communication what;
    private List<Environment> environments = new ArrayList<Environment>();

    /**
     * Default constructor.
     */
    AgreesTo() {
        super();
    }

    /**
     * Convenience constructor.
     * @param scenario the scenario
     */
    public AgreesTo( Scenario scenario ) {
        super( scenario );
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
     * Return the value of what.
     */
    public Communication getWhat() {
        return this.what;
    }

    /**
     * Set the value of what.
     * @param what The new value of what
     */
    public void setWhat( Communication what ) {
        this.what = what;
    }
}
