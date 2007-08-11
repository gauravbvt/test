// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.util.Date;

import com.mindalliance.channels.support.GUID;

/**
 * Certification by the user (a recognized authority) that either
 * another user is correctly associated with a person (Identity) or
 * that a person does in fact play all given roles for an organization
 * (Responsibility).
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision$
 */
public abstract class Certification extends Statement {

    private Date expirationDate;

    /**
     * Default constructor.
     */
    public Certification() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Certification( GUID guid ) {
        super( guid );
    }

    /**
     * Return the expiration date (null if no expiration).
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Set the expiration date.
     * @param expirationDate the expirationDate to set
     */
    public void setExpirationDate( Date expirationDate ) {
        this.expirationDate = expirationDate;
    }
}
