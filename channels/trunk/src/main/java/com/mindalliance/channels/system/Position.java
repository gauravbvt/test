// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import com.mindalliance.channels.User;

/**
 * A position in an organization.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @composed - - 1 ContactInfo
 * @navassoc - - 1 Role
 * @navassoc - - 1 User
 */
public class Position {

    private Role role;
    private User certifiedBy;
    private ContactInfo contactInfo;

    /**
     * Default constructor.
     */
    public Position() {
    }

    /**
     * Default constructor.
     * @param role the role in the organization
     */
    public Position( Role role ) {
        this();
        this.role = role;
    }

    /**
     * Return the value of role.
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * Set the value of role.
     * @param role The new value of role
     */
    public void setRole( Role role ) {
        this.role = role;
    }

    /**
     * Return the value of certifiedBy.
     */
    public User getCertifiedBy() {
        return this.certifiedBy;
    }

    /**
     * Set the value of certifiedBy.
     * @param certifiedBy The new value of certifiedBy
     */
    public void setCertifiedBy( User certifiedBy ) {
        this.certifiedBy = certifiedBy;
    }

    /**
     * Return the value of contactInfo.
     */
    public ContactInfo getContactInfo() {
        return this.contactInfo;
    }

    /**
     * Set the value of contactInfo.
     * @param contactInfo The new value of contactInfo
     */
    public void setContactInfo( ContactInfo contactInfo ) {
        this.contactInfo = contactInfo;
    }
}
