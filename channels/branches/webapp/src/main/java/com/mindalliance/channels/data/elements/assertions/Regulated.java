/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.elements.assertions;

import com.mindalliance.channels.data.Regulatable;
import com.mindalliance.channels.data.elements.scenario.Environment;
import com.mindalliance.channels.data.reference.Policy;
import com.mindalliance.channels.util.GUID;

/**
 * Assertion that some occurrence is regulated by a policy.
 * 
 * @author jf
 */
public class Regulated extends Assertion {

    /**
     * Policy that applies
     */
    private Policy policy; // by what policy
    private Environment environment; // only in this environment
                                        // (in any if null)
    private boolean forbids; // policy forbids else obligates

    public Regulated() {
        super();
    }

    public Regulated( GUID guid ) {
        super( guid );
    }

    /**
     * Return the Regulatable target of the assertion
     * 
     * @return
     */
    public Regulatable getRegulatable() {
        return (Regulatable) getAbout();
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
     * @return the forbids
     */
    public boolean isForbids() {
        return forbids;
    }

    /**
     * @param forbids the forbids to set
     */
    public void setForbids( boolean forbids ) {
        this.forbids = forbids;
    }

    /**
     * @return the policy
     */
    public Policy getPolicy() {
        return policy;
    }

    /**
     * @param policy the policy to set
     */
    public void setPolicy( Policy policy ) {
        this.policy = policy;
    }

}
