/*
 * Created on May 2, 2007
 */
package com.mindalliance.channels.data.elements.assertions;

import com.mindalliance.channels.data.Agreeable;
import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.reference.Environment;
import com.mindalliance.channels.util.GUID;

/**
 * @author jf Assertion that an exchange etc. has be agreed to by the
 *         organization who is the source of the information shared.
 */
public class AgreedTo extends Assertion {

    private Organization organization;
    private Environment environment; // Only in this environment
                                        // (in all if null)

    public AgreedTo() {
    }

    public AgreedTo( GUID guid ) {
        super( guid );
    }

    public Agreeable getAgreeable() {
        return (Agreeable) getAbout();
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
     * @return the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

}
