// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import com.mindalliance.channels.data.support.GUID;

/**
 * Request to add and invite a new user to join Channels.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class NewUserRequest extends UserRequest {

    private String name;
    private String emailAddress;

    /**
     * Default constructor.
     */
    public NewUserRequest() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public NewUserRequest( GUID guid ) {
        super( guid );
    }

    /**
     * Return the email address.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Set the email address.
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress( String emailAddress ) {
        this.emailAddress = emailAddress;
    }

    /**
     * Return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     * @param name the name to set
     */
    public void setName( String name ) {
        this.name = name;
    }
}
