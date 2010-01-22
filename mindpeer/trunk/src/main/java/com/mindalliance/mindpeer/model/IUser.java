// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import org.springframework.security.userdetails.UserDetails;

/**
 * Interface version of User, for lack of AspectJ compile-time support for security aspects.
 * @todo Figure out how to compile correctly with AspectJ
 */
public interface IUser extends UserDetails {

    /**
     * Return the state of this object.
     * @return the value of state
     */
    User.State getState();

    /**
     * Sets the enabled state of this user.
     * @param enabled the new enabled state. Setting to false will mark the user as 'terminated'.
     *
     */
    void setEnabled( boolean enabled );

    /**
     * Sets the username of this user.
     * @param username the new username value.
     */
    void setUsername( String username );

    /**
     * Return if this user is an administrator.
     * @return the value of admin
     */
    boolean isAdmin();

    /**
     * Sets SHA1 checksum of the user's password.
     * @param password the new value.
     */
    void setPassword( String password );

    /**
     * Return the user's email.
     * @return the value of email
     */
    String getEmail();

    /**
     * Sets the email of this user.
     * @param email the new email value.
     */
    void setEmail( String email );

    /**
     * Return the confirmation number.
     * @return the value of confirmationNumber, null if the user was confirmed
     */
    Long getConfirmation();

    /**
     * Sets the confirmation number for enabling the user. When set, a message will be sent to the
     * the email address containing a link back to MindPeer with this number. Account is considered
     * unconfirmed (and disabled) until the link is followed.
     * @param confirmation the new confirmation number value.
     */
    void setConfirmation( Long confirmation );

    /**
     * Return the user's  profile.
     * @return the value of profile
     */
    Profile getProfile();

    /**
     * Sets the profile of this User.
     * @param profile the new profile value.
     */
    void setProfile( Profile profile );
}
