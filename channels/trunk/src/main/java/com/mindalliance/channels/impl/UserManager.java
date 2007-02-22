// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.impl;

import java.util.HashMap;
import java.util.Map;

import org.acegisecurity.annotation.Secured;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;

/**
 * In-memory user manager.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class UserManager extends AbstractJavaBean
        implements UserDetailsService {

    private Map<String,UserImpl> users = new HashMap<String,UserImpl>();

    /**
     * Default constructor.
     */
    public UserManager() {
    }

    /**
     * Return the value of users.
     */
    public Map<String, UserImpl> getUsers() {
        return this.users;
    }

    /**
     * Set the value of users.
     * @param users The new value of users
     */
    @Secured( {"ROLE_ADMIN" } )
    public void setUsers( Map<String, UserImpl> users ) {

        if ( users == null )
            throw new NullPointerException();

        this.users = users;
    }

    /**
     * Add a user.
     * @param user the user to add
     */
    @Secured( {"ROLE_ADMIN" } )
    public void addUser( UserImpl user ) {
        this.users.put( user.getUsername(), user );
    }

    /**
     * Remove a user.
     * @param user the user to remove
     */
    @Secured( {"ROLE_ADMIN" } )
    public void removeUser( UserImpl user ) {
        this.users.remove( user.getUsername() );
    }

    /**
     * Return the user details for given username.
     * @param username the username
     * @see UserDetailsService#loadUserByUsername(java.lang.String)
     */
    public UserDetails loadUserByUsername( String username ) {

        UserDetails result = this.users.get( username );
        if ( result == null )
            throw new UsernameNotFoundException( username );

        return result;
    }

}
