/*
 * Created on May 3, 2007
 */
package com.mindalliance.channels.data.user;

/**
 * Request to add and invite a new user to join Channels.
 * 
 * @author jf
 */
public class NewUserRequest extends UserRequest {

    private String name;
    private String emailAddress;

    public NewUserRequest() {
        super();
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress( String emailAddress ) {
        this.emailAddress = emailAddress;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName( String name ) {
        this.name = name;
    }

}
