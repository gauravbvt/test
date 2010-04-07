// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.dao;

import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Holder of raw info from the property file.
 */
public final class UserInfo implements Serializable {

    /**
     * The admin role string.
     */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    /**
     * The planner role string.
     */
    public static final String ROLE_PLANNER = "ROLE_PLANNER";

    /**
     * The user role string. Implied if user is listed in user list.
     */
    public static final String ROLE_USER = "ROLE_USER";

    /** The serialization constant. */
    private static final long serialVersionUID = -7672037364749386902L;

    /** The username. */
    private String username;

    /** The passwrod. */
    private String password;

    /** The fullName. */
    private String fullName;

    /** The email. */
    private String email;

    /** The user's access (ROLE_USER|ROLE_PLANNER|ROLE_ADMIN), indexed by plan uri. */
    private Map<String,String> planAccess = new HashMap<String, String>();

    /** The user's global access role (ROLE_USER|ROLE_PLANNER|ROLE_ADMIN). */
    private List<String> globalAccess = new ArrayList<String>();


    //---------------------------------
    public UserInfo( String username, String values ) {
        this.username = username;
        StringTokenizer tokens = new StringTokenizer( values, "," );

        password = tokens.nextToken();
        fullName = tokens.nextToken();
        email = tokens.nextToken();

        while ( tokens.hasMoreTokens() ) {
            String token = tokens.nextToken();
            if ( token.startsWith( "[" ) && token.endsWith( "]" ) ) {
                // e.g. [mindalliance.com/channels/plans/sci|ROLE_PLANNER]
                // e.g. [mindalliance.com/channels/plans/sci]
                StringTokenizer access =
                        new StringTokenizer( token.substring( 1, token.length() - 1 ), "|" );

                String uri = access.nextToken();
                planAccess.put( uri,
                    access.hasMoreTokens() && ROLE_PLANNER.equals( access.nextToken() ) ?
                        ROLE_PLANNER : ROLE_USER );

            } else if ( token.equals( ROLE_ADMIN ) || token.equals( ROLE_PLANNER )
                                                   || token.equals( ROLE_USER ) )
                globalAccess.add( token );

            else
                LoggerFactory.getLogger( getClass() ).warn(
                        "Discarding invalid user definition part: {}", token );
        }

        simplify();
    }

    /**
     * Remove redundant declarations.
     */
    private void simplify() {
        if ( globalAccess.contains( ROLE_ADMIN ) ) {
            globalAccess.remove( ROLE_PLANNER );
            globalAccess.remove( ROLE_USER );
            planAccess.clear();

        } else if ( globalAccess.contains( ROLE_PLANNER ) ) {
            globalAccess.remove( ROLE_USER );
            planAccess.clear();

        } else if ( globalAccess.contains( ROLE_USER ) )
            for ( String uri : new HashSet<String>( planAccess.keySet() ) )
                if ( ROLE_USER.equals( planAccess.get( uri ) ) )
                    planAccess.remove( uri );
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName( String fullName ) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Set the password.
     * @param password unencrypted.
     */
    public void setPassword( String password ) {
        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        this.password = encoder.encodePassword( password, null );
    }

    public String getUsername() {
        return username;
    }

    /**
     * Test if this user can access the given plan uri.
     * @param uri the plan uri
     * @return true if user is authorized for that plan
     */
    public boolean isUser( String uri ) {
        return !globalAccess.isEmpty() || planAccess.containsKey( uri );
    }

    /**
     * Test if this user can plan the given plan uri.
     * @param uri the plan uri
     * @return true if user is a planner for that plan
     */
    public boolean isPlanner( String uri ) {
        return globalAccess.contains( ROLE_ADMIN )
            || globalAccess.contains( ROLE_PLANNER )
            || ROLE_PLANNER.equals( planAccess.get( uri ) );
    }

    public boolean isAdmin() {
        return globalAccess.contains( ROLE_ADMIN );
    }

    /**
     * Test if a user has any access.
     * @return true if the user is authorized for at least a plan
     */
    public boolean isEnabled() {
        return !globalAccess.isEmpty() || !planAccess.isEmpty();
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        buffer.append( password );
        buffer.append( ',' );
        buffer.append( fullName );
        buffer.append( ',' );
        buffer.append( email );
        for ( Map.Entry<String, String> access : planAccess.entrySet() ) {
            buffer.append( ",[" );
            buffer.append( access.getKey() );
            if ( access.getValue().equals( ROLE_PLANNER ) ) {
                buffer.append( '|' );
                buffer.append( ROLE_PLANNER );
            }
            buffer.append( ']' );
        }
        for ( String access : globalAccess ) {
            buffer.append( ',' );
            buffer.append( access );
        }

        return buffer.toString();
    }
}
