// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.beans.PropertyVetoException;
import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.User;
import com.mindalliance.channels.reference.ClearanceType;
import com.mindalliance.channels.reference.CredentialType;

/**
 * An individual who holds one or more positions in one or more organizations.
 * A person in the project may also be a user of the system.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @navassoc * - 1 User
 */
public class Person extends Agent {

    private User user;
    private Set<CredentialType> credentials = new TreeSet<CredentialType>();
    private ClearanceType clearance;
    /**
     * Default constructor.
     */
    public Person() {
        super();
    }

    /**
     * Default constructor.
     * @param name the name of this person
     * @throws PropertyVetoException if name clashes with an existing person
     */
    public Person( String name ) throws PropertyVetoException {
        super( name );
    }

    /**
     * Return the value of clearance.
     */
    public ClearanceType getClearance() {
        return this.clearance;
    }

    /**
     * Set the value of clearance.
     * @param clearance The new value of clearance
     */
    public void setClearance( ClearanceType clearance ) {
        this.clearance = clearance;
    }

    /**
     * Return the value of credentials.
     */
    public Set<CredentialType> getCredentials() {
        return this.credentials;
    }

    /**
     * Set the value of credentials.
     * @param credentials The new value of credentials
     */
    public void setCredentials( Set<CredentialType> credentials ) {
        this.credentials = credentials;
    }

    /**
     * Add a credential.
     * @param credential the credential
     */
    public void addCredential( CredentialType credential ) {
        this.credentials.add( credential );
    }

    /**
     * Remove a credential.
     * @param credential the credential
     */
    public void removeCredential( CredentialType credential ) {
        this.credentials.remove( credential );
    }

    /**
     * Return the value of user.
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Set the value of user.
     * @param user The new value of user
     */
    public void setUser( User user ) {
        this.user = user;
    }
}
