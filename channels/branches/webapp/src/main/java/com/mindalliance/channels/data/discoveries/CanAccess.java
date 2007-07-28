// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.discoveries;

import com.mindalliance.channels.data.definitions.Situation;
import com.mindalliance.channels.data.models.Assertion;
import com.mindalliance.channels.data.profiles.Contactable;
import com.mindalliance.channels.data.support.GUID;

/**
 * Assertion that someone has been granted access to someone else.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class CanAccess extends Assertion {

    private Contactable contact;
    private Situation environment;

    /**
     * Default constructor.
     */
    public CanAccess() {
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public CanAccess( GUID guid ) {
        super( guid );
    }

    /**
     * Return the contact (access to what).
     */
    public Contactable getContact() {
        return contact;
    }

    /**
     * Set the contact.
     * @param contact the contact
     */
    public void setContact( Contactable contact ) {
        this.contact = contact;
    }

    /**
     * Return the environment in which this assertio applies.
     */
    public Situation getEnvironment() {
        return environment;
    }

    /**
     * Set the environment in which this assertio applies.
     * @param environment the environment
     */
    public void setEnvironment( Situation environment ) {
        this.environment = environment;
    }

}
