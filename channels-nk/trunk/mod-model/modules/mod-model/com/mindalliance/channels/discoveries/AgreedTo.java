// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.discoveries;

import com.mindalliance.channels.definitions.Organization;
import com.mindalliance.channels.definitions.Situation;
import com.mindalliance.channels.models.Assertion;
import com.mindalliance.channels.support.GUID;

/**
 * Assertion that an exchange etc. has be agreed to by the
 * organization who is the source of the information shared.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class AgreedTo extends Assertion {

    private Organization organization;
    private Situation environment;

    /**
     * Default constructor.
     */
    public AgreedTo() {
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public AgreedTo( GUID guid ) {
        super( guid );
    }

    /**
     * Return the environment where the agreement applies.
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
     * Return the organization.
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Set the organization.
     * @param organization the organization to set
     */
    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

}
