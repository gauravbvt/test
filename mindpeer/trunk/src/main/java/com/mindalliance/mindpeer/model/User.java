// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.annotation.Secured;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

/**
 * A MindPeer user and its relevant information.
 */
@Entity
public class User extends ModelObject implements IUser {

    /** Minimum confirmation number. */
    private static final long TICKET_MIN = 1000L;

    /** Range of confirmation number (maximum = MIN + RANGE). */
    private static final double TICKET_RANGE = 1000000.0;

    /** State of a user account. */
    public enum State {
        /** User is new, but email confirmation link wasn't used. */
        Unconfirmed,

        /** User is active, email confirmed. */
        Registered,

        /** User has been terminated by admin. */
        Terminated
    }

    /** The user name (not the fullname). Unique for active users. */
    @Column( unique = true, nullable = false )
    private String username;

    /** True when user is active. */
    @Column( nullable = false )
    private boolean enabled = true;

    /** The SHA-encoded password. */
    private String password;

    /** The validated email address. */
    private String email;

    /** Confimation number for activation. Null after account has been activated. */
    @Column( nullable = true )
    private Long confirmation;

    @OneToOne( cascade = CascadeType.ALL, optional = true )
    private Profile profile;

    //=======================================
    /**
     * Create a new User instance.
     */
    public User() {
        confirmation = TICKET_MIN + Math.round( Math.random() * TICKET_RANGE );
        profile = new Profile( this );
    }

    /**
     * Return the state of this object.
     * @return the value of state
     */
    public State getState() {
        return enabled ?
                    confirmation == null ? State.Registered
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
    @Secured( { "ROLE_ADMIN", "ROLE_RUN_AS_SYSTEM" } )
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
    @Secured( "ROLE_ADMIN" )
    public void setUsername( String username ) {
        this.username = username;
    }

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    public GrantedAuthority[] getAuthorities() {
        List<GrantedAuthority> result = new ArrayList<GrantedAuthority>( 2 );
        result.add( new GrantedAuthorityImpl( "ROLE_USER" ) );
        if ( isAdmin() )
            result.add( new GrantedAuthorityImpl( "ROLE_ADMIN" ) );

        return result.toArray( new GrantedAuthority[ result.size() ] );
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
    @Secured( { "ROLE_ADMIN", "USER" } )
    public void setPassword( String password ) {
        this.password = password;
    }

    /**
     * Return the user's email.
     * @return the value of email
     */
    @Secured( { "ROLE_USER" } )
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of this user.
     * @param email the new email value.
     */
    @Secured( { "ROLE_ADMIN", "USER" } )
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
    @Secured( { "ROLE_ADMIN", "ROLE_RUN_AS_SYSTEM" } )
    public void setConfirmation( Long confirmation ) {
        this.confirmation = confirmation;
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
    @Secured( "ROLE_RUN_AS_SYSTEM" )
    public void setProfile( Profile profile ) {
        this.profile = profile;
    }
}
