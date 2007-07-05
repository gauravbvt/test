// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.user;

import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.Properties;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.annotation.Secured;
import org.acegisecurity.userdetails.UserDetails;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.Named;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.resources.Person;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * A user of the system.
 * <p>
 * The current user is obtainable from the acegi security context by
 * using the following code from anywhere:
 * </p>
 *
 * <pre>
 * (User) SecurityContextHolder.getContext()
 *                 .getAuthentication().getPrincipal();
 * </pre>
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision: 103 $
 * @extends User
 */
public class UserImpl extends AbstractJavaBean implements User, UserDetails {

    private String username;
    private String password;

    /**
     * The full name of the user.
     * @todo redundant with Person
     */
    private String name;

    /**
     * The user's email address.
     * @todo redundant with Person
     */
    private String email;
    private Properties preferences;

    /**
     * Who the user is and the roles he/she plays in what
     * organizations etc.
     */
    private Person person;

    private boolean enabled = true;
    private String[] grantedAuthorities;
    private transient GrantedAuthority[] authorities;

    /**
     * Default bean constructor.
     */
    public UserImpl() {
        preferences = new Properties();
    }

    /**
     * Default constructor.
     *
     * @param name the full name of the user
     * @param username the login id of the user
     * @param password the original password.
     *      User may change it later.
     * @param authorities the initial granted roles.
     *      Supervisor may adjust later.
     */
    public UserImpl(
        String name, String username, String password, String[] authorities ) {

        this.name = name;
        this.username = username;
        this.password = password;
        setGrantedAuthorities( authorities );
    }

    /**
     * Compares this named object with the specified named object for
     * order.
     *
     * @param named the named object to compare to
     */
    public int compareTo( Named named ) {
        return getName().compareTo( named.getName() );
    }

    /**
     * Return the value of email.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Set the value of email.
     *
     * @param email The new value of email
     */
    @Secured( { "ROLE_ADMIN", "THIS_USER" } )
    public void setEmail( String email ) {
        this.email = email;
    }

    /**
     * Return the value of accountNonDisabled.
     *
     * @return always true
     */
    @PropertyOptions(ignore=true)
    public boolean isAccountNonDisabled() {
        return isEnabled();
    }

    /**
     * Return the value of accountNonExpired.
     */
    @PropertyOptions(ignore=true)
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Return the value of accountNonLocked.
     *
     * @return always true
     */
    @PropertyOptions(ignore=true)
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Return the value of credentialsNonExpired.
     *
     * @return always true
     */
    @PropertyOptions(ignore=true)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Return the value of enabled.
     *
     * @return true by default, may be changed by administrators
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Set the value of enabled.
     *
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
     *
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
     *
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
    @PropertyOptions(ignore=true)
    public synchronized GrantedAuthority[] getAuthorities() {
        if ( this.authorities == null ) {
            this.authorities =
                new GrantedAuthority[ this.grantedAuthorities.length ];
            for ( int i = 0; i < this.grantedAuthorities.length; i++ )
                this.authorities[i] = new GrantedAuthorityImpl(
                        this.grantedAuthorities[i] );
        }

        return this.authorities;
    }

    /**
     * Return the value of grantedAuthorities.
     */
    @PropertyOptions(ignore=true)
    public String[] getGrantedAuthorities() {
        return this.grantedAuthorities;
    }

    /**
     * Set the value of grantedAuthorities.
     *
     * @param grantedAuthorities The new value of grantedAuthorities
     */
    @Secured( { "ROLE_ADMIN" } )
    public synchronized void setGrantedAuthorities(
            String[] grantedAuthorities ) {

        this.grantedAuthorities = grantedAuthorities;
        this.authorities = null;
    }

    /**
     * Set the user's full name.
     * @param name the name to set
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Return the full name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the user's preferences.
     * @return the preferences
     */
    @PropertyOptions(ignore=true)
    public Properties getPreferences() {
        // TODO  add a proper property editor
        return preferences;
    }

    /**
     * Set the user's preferences.
     * @param preferences the preferences to set
     */
    public void setPreferences( Properties preferences ) {
        this.preferences = preferences;
    }

    /**
     * Return the person corresponding to this user.
     * @todo fix dependency cycle
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Set the person corresponding to this user.
     * @todo fix dependency cycle
     * @param person the person to set
     */
    public void setPerson( Person person ) {
        this.person = person;
    }

    /**
     * Test if this user has administrator rights.
     */
    @PropertyOptions(ignore=true)
    public boolean isAdmin() {
        return Arrays.asList(
                this.getGrantedAuthorities() ).contains( User.ADMIN_ROLE );
    }

    /**
     * Test if this user is a standard user.
     */
    @PropertyOptions(ignore=true)
    public boolean isStandardUser() {
        return Arrays.asList(
                this.getGrantedAuthorities() ).contains( User.USER_ROLE )
            || isAdmin();
    }

    /**
     * Return a String representation of the User.
     * For now this is the username.
     */
    public String toString() {
        return getName();
    }

}
