// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.impl;

import java.beans.PropertyVetoException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.annotation.Secured;
import org.acegisecurity.userdetails.UserDetails;

import com.mindalliance.channels.Group;
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
 */
public class UserImpl extends AbstractJavaBean
    implements User, UserDetails, Comparable<UserImpl> {

    private String username;
    private String password;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean accountNonDisabled;
    private boolean credentialsNonExpired;

    private String[] grantedAuthorities;
    private transient GrantedAuthority[] authorities;

    private String name;
    private String email;
    private Set<Group> groups = new HashSet<Group>();

    /**
     * Default bean constructor.
     */
    public UserImpl() {
    }

    /**
     * Default constructor.
     * @param username the login id of the user
     * @param password the original password.
     * User may change it later.
     * @param authorities the initial granted roles.
     * Supervisor may adjust later.
     */
    public UserImpl(
            String username, String password,
            GrantedAuthority[] authorities ) {

        this();
        this.username = username;
        this.authorities = authorities;
        this.password = password;
    }

    /**
     * Test if this user is a member of a group.
     * @param group the group
     * @see User#isMemberOf(Group)
     */
    public boolean isMemberOf( Group group ) {
        // TODO Add public group?
        return this.groups.contains( group );
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
    @Secured( { "ROLE_ADMIN","ROLE_SELF" } )
    public void setEmail( String email ) {
        this.email = email;
    }

    /**
     * Return the value of groups.
     */
    public final Set<Group> getGroups() {
        return Collections.unmodifiableSet( this.groups );
    }

    /**
     * Set the value of groups.
     * @param groups The new value of groups
     */
    @Secured( { "ROLE_ADMIN" } )
    public void setGroups( Set<Group> groups ) {
        this.groups = groups;
    }

    /**
     * Remove a group from the user's list.
     * @param group the group
     */
    @Secured( { "ROLE_ADMIN" } )
    public void removeGroup( Group group ) {
        this.groups.remove( group );
    }

    /**
     * Add a group to the user's list.
     * @param group the group
     */
    @Secured( { "ROLE_ADMIN" } )
    public void addGroup( Group group ) {
        this.groups.add( group );
    }

    /**
     * Return the value of name.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Set the full name of the user.
     * @param name The new value of name
     */
    @Secured( { "ROLE_ADMIN","ROLE_SELF" } )
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Return the value of accountNonDisabled.
     */
    public boolean isAccountNonDisabled() {
        return this.accountNonDisabled;
    }

    /**
     * Set the value of accountNonDisabled.
     * @param accountNonDisabled The new value of accountNonDisabled
     */
    @Secured( { "ROLE_ADMIN" } )
    public void setAccountNonDisabled( boolean accountNonDisabled ) {
        this.accountNonDisabled = accountNonDisabled;
    }

    /**
     * Return the value of accountNonLocked.
     */
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    /**
     * Set the value of accountNonLocked.
     * @param accountNonLocked The new value of accountNonLocked
     */
    @Secured( { "ROLE_ADMIN" } )
    public void setAccountNonLocked( boolean accountNonLocked ) {
        this.accountNonLocked = accountNonLocked;
    }

    /**
     * Return the value of credentialsNonExpired.
     */
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    /**
     * Set the value of credentialsNonExpired.
     * @param credentialsNonExpired The new value of credentialsNonExpired
     */
    @Secured( { "ROLE_ADMIN" } )
    public void setCredentialsNonExpired( boolean credentialsNonExpired ) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    /**
     * Return the value of enabled.
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
    @Secured( { "ROLE_ADMIN", "ROLE_SELF" } )
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
    @Secured( { "ROLE_ADMIN" } )
    public void setUsername( String username ) throws PropertyVetoException {
        this.username = username;
    }

    /**
     * Return the value of accountNonExpired.
     */
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    /**
     * Set the value of accountNonExpired.
     * @param accountNonExpired The new value of accountNonExpired
     */
    @Secured( { "ROLE_ADMIN" } )
    public void setAccountNonExpired( boolean accountNonExpired ) {
        this.accountNonExpired = accountNonExpired;
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
     * Sort alphabetically ignoring case.
     * @param o a user to compare to.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo( UserImpl o ) {
        return getUsername().compareToIgnoreCase( o.getUsername() );
    }

    /**
     * Return true if this is equal to another object.
     * Tests if usernames are the same.
     * @param obj the object to compare to
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        return obj != null
            && obj.getClass() == this.getClass()
            && getUsername().equals( ( (UserImpl) obj ).getUsername() );
    }

    /**
     * Return a unique code to use in hashes and maps.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }

    /**
     * Return a string representation of this user (the username).
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getUsername();
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
