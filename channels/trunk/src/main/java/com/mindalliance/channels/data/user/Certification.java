/*
 * Created on May 3, 2007
 */
package com.mindalliance.channels.data.user;

import java.util.Date;

/**
 * Certification by the user (a recognized authority) that either
 * another user is correctly associated with a person (Identity) or
 * that a person does in fact play all given roles for an organization
 * (Responsibility).
 * 
 * @author jf
 */
abstract public class Certification extends Statement {

    private Date expirationDate; // null if no expiration

    public Certification() {
        super();
    }

    /**
     * @return the expirationDate
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * @param expirationDate the expirationDate to set
     */
    public void setExpirationDate( Date expirationDate ) {
        this.expirationDate = expirationDate;
    }

}
