// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.models;

import com.mindalliance.channels.definitions.Information;
import com.mindalliance.channels.support.GUID;

/**
 * Assertion made about some scenario element known of by someone.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Known extends Assertion {

    private Information information;
    private Knowledgeable knower;

    /**
     * Default constructor.
     */
    public Known() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Known( GUID guid ) {
        super( guid );
    }

    /**
     * Return the information (what's known).
     */
    public Information getInformation() {
        return information;
    }

    /**
     * Set the information.
     * @param information the information
     */
    public void setInformation( Information information ) {
        this.information = information;
    }

    /**
     * Return the value of knower.
     * Known by whom: an agent ("whoever does this task knows this"),
     * role ("anyone in this role knows this") or team who knows this.
     */
    public Knowledgeable getKnower() {
        return knower;
    }

    /**
     * Set the value of knower.
     * @param knower The new value of knower
     */
    public void setKnower( Knowledgeable knower ) {
        this.knower = knower;
    }

}
