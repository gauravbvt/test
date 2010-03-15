// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// TODO Secure correctly for new account creation

/**
 * A MindPeer user and its relevant information.
 */
@Entity
public class User extends ModelObject implements IUser {

    /** Minimum confirmation number. */
    public static final long TICKET_MIN = 1000L;

    /** Range of confirmation number (maximum = MIN + RANGE). */
    public static final double TICKET_RANGE = 1000000.0;

    /** State of a user account. */
    public enum State {
        /** User is new, but email confirmation link wasn't used. */
        Unconfirmed,

        /** User is active, email confirmed. */
        Registered,

        /** User has been terminated by admin. */
        Terminated
    }

    /** The user name (not the full name). Unique for all users. */
    @Column( unique = true, nullable = false )
    private String username;

    /** True when user is active. */
    @Column( nullable = false )
    private boolean enabled = true;

    /** The SHA-encoded password. */
    private String password;

    /** The validated email address. Unique for all users.*/
    @Column( unique = true, nullable = false )
    private String email;

    /** Confimation number for activation. Null after account has been activated. */
    @Column( nullable = true )
    private Long confirmation;

    /** True after the user has visited the confirmation link. */
    private boolean confirmed;

    @OneToOne( cascade = CascadeType.ALL, optional = true )
    private Profile profile;

    @OneToMany( cascade = CascadeType.MERGE, mappedBy = "user" )
    private List<Focus> focusList = new ArrayList<Focus>();

    //=======================================
    /**
     * Create a new User instance.
     */
    public User() {
        confirmation = TICKET_MIN + Math.round( Math.random() * TICKET_RANGE );
        profile = new Profile( this );

 //       add( new Focus( "Untitled" ) );
    }

    public void add( Focus focus ) {
        focus.setUser( this );
        focusList.add( focus );
    }

    /**
     * Return the state of this object.
     * @return the value of state
     */
    public State getState() {
        return enabled ?
                    confirmed ? State.Registered
                              : State.Unconfirmed
                  : State.Terminated;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled state of this user.
     * @param enabled the new enabled state. Setting to false will mark the user as 'terminated'.
     *
     */
    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }

    /**
     * Returns the username used to authenticate the user. Cannot return <code>null</code>.
     *
     * @return the username (never <code>null</code>)
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of this user.
     * @param username the new username value.
     */
    public void setUsername( String username ) {
        this.username = username;
    }

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    public Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> result = new ArrayList<GrantedAuthority>( 2 );
        result.add( new GrantedAuthorityImpl( "ROLE_USER" ) );
        if ( isAdmin() )
            result.add( new GrantedAuthorityImpl( "ROLE_ADMIN" ) );

        return result;
    }

    /**
     * Return if this user is an administrator.
     * @return the value of admin
     */
    public boolean isAdmin() {
        // TODO make this portable across DBs
        return getId() == 1L ;
    }

    /**
     * Return the SHA1 checksum of the user's password.
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot be authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    public boolean isAccountNonExpired() {
        return !State.Terminated.equals( getState() );
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    public boolean isAccountNonLocked() {
        return State.Registered.equals( getState() );
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired credentials prevent
     * authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Sets SHA1 checksum of the user's password.
     * @param password the new value.
     */
    public void setPassword( String password ) {
        this.password = password;
    }

    /**
     * Return the user's email.
     * @return the value of email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of this user.
     * @param email the new email value.
     */
    public void setEmail( String email ) {
        this.email = email;
    }

    /**
     * Return the confirmation number.
     * @return the value of confirmationNumber, null if the user was confirmed
     */
    public Long getConfirmation() {
        return confirmation;
    }

    /**
     * Sets the confirmation number for enabling the user. When set, a message will be sent to the
     * the email address containing a link back to MindPeer with this number. Account is considered
     * unconfirmed (and disabled) until the link is followed.
     * @param confirmation the new confirmation number value.
     */
    public void setConfirmation( Long confirmation ) {
        this.confirmation = confirmation;
    }

    /**
     * Test if this user is locked and waiting for a specific confirmation number.
     *
     * @param received the given confirmation
     * @return true if numbers match
     */
    public boolean isConfirmableWith( Long received ) {
        return confirmation != null && confirmation.equals( received );
    }

    /**
     * Return the user's  profile.
     * @return the value of profile
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * Sets the profile of this User.
     * @param profile the new profile value.
     */
    public void setProfile( Profile profile ) {
        profile.setUser( this );
        this.profile = profile;
    }

    /**
     * Return true if the user visited the confirmation link at least once.
     * @return the value of confirmed
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Mark the user as confirmed (or requiring confirmation.
     * @param confirmed the new confirmed status.
     *
     */
    public void setConfirmed( boolean confirmed ) {
        this.confirmed = confirmed;
    }

    /**
     * Return the user's focus list.
     * @return the value of focusList
     */
    public List<Focus> getFocusList() {
        return focusList;
    }

    /**
     * Sets the focus list of this user.
     * @param focusList the new focus list value.
     *
     */
    public void setFocusList( List<Focus> focusList ) {
        if ( focusList.isEmpty() )
            throw new IllegalArgumentException( "Must have at least one focus" );
        this.focusList = focusList;
    }

    /**
     * Return the user's default focus.
     * @return the default focus
     */
    public Focus getDefaultFocus() {
        return focusList.get( 0 );
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "User[" + getId() + ":" + username + "]";
    }
}
