// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.beans.PropertyVetoException;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.annotation.Secured;
import org.acegisecurity.userdetails.UserDetails;

import com.mindalliance.channels.User;

/**
 * A user of the system.
 *
 * <p>The current user is obtainable from the acegi security
 * context by using the following code from anywhere:</p>
 *
 * <pre>(User) SecurityContextHolder.getContext()
 *                .getAuthentication().getPrincipal();</pre>
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @extends User
 */
public class UserImpl extends SystemObject
    implements User, UserDetails {

    private String username;
    private String password;

    private String email;

    private boolean enabled = true;
    private String[] grantedAuthorities;
    private transient GrantedAuthority[] authorities;

    /**
     * Default bean constructor.
     */
    public UserImpl() {
    }

    /**
     * Default constructor.
     * @param name the full name of the user
     * @param username the login id of the user
     * @param password the original password.
     * User may change it later.
     * @param authorities the initial granted roles.
     * Supervisor may adjust later.
     * @throws PropertyVetoException if name conflicts with others
     */
    public UserImpl(
            String name,
            String username, String password,
            String[] authorities )
        throws PropertyVetoException {

        super( name );
        this.username = username;
        this.password = password;
        setGrantedAuthorities( authorities );
    }

    /**
     * Return the value of email.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Set the value of email.
     * @param email The new value of email
     */
    @Secured( { "ROLE_ADMIN", "THIS_USER" } )
    public void setEmail( String email ) {
        this.email = email;
    }

    /**
     * Return the value of accountNonDisabled.
     * @return always true
     */
    public boolean isAccountNonDisabled() {
        return isEnabled();
    }

    /**
     * Return the value of accountNonExpired.
     */
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Return the value of accountNonLocked.
     * @return always true
     */
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Return the value of credentialsNonExpired.
     * @return always true
     */
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Return the value of enabled.
     * @return true by default, may be changed by administrators
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Set the value of enabled.
     * @param enabled The new value of enabled
     */
    @Secured( { "ROLE_ADMIN" } )
    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }

    /**
     * Return the value of password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Set the value of password.
     * @param password The new value of password
     */
    @Secured( { "ROLE_ADMIN", "USER" } )
    public void setPassword( String password ) {
        this.password = password;
    }

    /**
     * Return the value of username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Set the username.
     * @param username the username
     * @throws PropertyVetoException if user manager objects
     */
    @Secured( { "ROLE_ADMIN", "USER" } )
    public void setUsername( String username ) throws PropertyVetoException {
        this.username = username;
    }

    /**
     * Return the value of attached to this user.
     */
    public synchronized GrantedAuthority[] getAuthorities() {
        if ( this.authorities == null ) {
            this.authorities =
                new GrantedAuthority[ this.grantedAuthorities.length ];
            for ( int i = 0 ; i < this.grantedAuthorities.length ; i++ )
                this.authorities[ i ] =
                    new GrantedAuthorityImpl( this.grantedAuthorities[ i ] );
        }

        return this.authorities;
    }

    /**
     * Return the value of grantedAuthorities.
     */
    public String[] getGrantedAuthorities() {
        return this.grantedAuthorities;
    }

    /**
     * Set the value of grantedAuthorities.
     * @param grantedAuthorities The new value of grantedAuthorities
     */
    @Secured( { "ROLE_ADMIN" } )
    public synchronized void setGrantedAuthorities(
            String[] grantedAuthorities ) {

        this.grantedAuthorities = grantedAuthorities;
        this.authorities = null;
    }
}
