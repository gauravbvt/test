// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.discoveries;

import com.mindalliance.channels.definitions.Policy;
import com.mindalliance.channels.definitions.Situation;
import com.mindalliance.channels.models.Assertion;
import com.mindalliance.channels.support.GUID;

/**
 * Assertion that some occurrence is regulated by a policy.
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Regulated extends Assertion {

    /**
     * Policy that applies.
     */
    private Policy policy;

    /**
     * Only in this situation (in any if null).
     */
    private Situation situation;

    /**
     * Policy forbids else obligates.
     */
    private boolean forbids;

    /**
     * Default constructor.
     */
    public Regulated() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Regulated( GUID guid ) {
        super( guid );
    }

    /**
     * Return the situation.
     */
    public Situation getSituation() {
        return situation;
    }

    /**
     * Set the situation.
     * @param situation the situation to set
     */
    public void setSituation( Situation situation ) {
        this.situation = situation;
    }

    /**
     * Return if the policy forbids.
     */
    public boolean isForbids() {
        return forbids;
    }

    /**
     * Set if the policy forbids.
     * @param forbids the forbids to set
     */
    public void setForbids( boolean forbids ) {
        this.forbids = forbids;
    }

    /**
     * Return the policy.
     */
    public Policy getPolicy() {
        return policy;
    }

    /**
     * Set the policy.
     * @param policy the policy to set
     */
    public void setPolicy( Policy policy ) {
        this.policy = policy;
    }

}
