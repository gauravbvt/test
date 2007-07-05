/*
 * Created on Apr 27, 2007
 */
package com.mindalliance.channels.data.elements.assertions;

import com.mindalliance.channels.data.Connected;
import com.mindalliance.channels.data.components.Contactable;
import com.mindalliance.channels.data.elements.scenario.Environment;
import com.mindalliance.channels.util.GUID;

/**
 * Assertion that someone has been granted access to someone else.
 * 
 * @author jf
 */
public class CanAccess extends Assertion {

    private Contactable contact; // access to what
    private Environment environment; // in what environment (null
                                        // if all environments)

    public CanAccess() {
    }

    public CanAccess( GUID guid ) {
        super( guid );
    }

    public Connected getConnected() {
        return (Connected) getAbout();
    }

    /**
     * @return the contact
     */
    public Contactable getContact() {
        return contact;
    }

    /**
     * @param contact the contact to set
     */
    public void setContact( Contactable contact ) {
        this.contact = contact;
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

}
