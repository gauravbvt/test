// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.model.Plan;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import java.io.Serializable;
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

    /** The password. */
    private String password;

    /** The fullName. */
    private String fullName;

    /** The email. */
    private String email;

    /** The user's access (ROLE_USER|ROLE_PLANNER|ROLE_ADMIN), indexed by plan uri. */
    private Map<String,String> planAccess = new HashMap<String, String>();

    /** The user's global access role (ROLE_USER|ROLE_PLANNER|ROLE_ADMIN|null). */
    private String globalAccess;


    //---------------------------------
    public UserInfo( String username, String values ) {
        this.username = username;
        StringTokenizer tokens = new StringTokenizer( values, "," );

        password = tokens.nextToken();
        fullName = tokens.nextToken();
        email = tokens.nextToken();
        globalAccess = null;

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
                globalAccess = token;

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
        if ( ROLE_ADMIN.equals( globalAccess ) || ROLE_PLANNER.equals( globalAccess ) ) {
            planAccess.clear();
        } else if ( ROLE_USER.equals( globalAccess ) )
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
     * Test if this user is a user of all plans.
     * @return true if so
     */
    public boolean isUser() {
        return ROLE_USER.equals( globalAccess ) || isPlanner();
    }

    /**
     * Test if this user is a planner for all plans.
     * @return true if so
     */
    public boolean isPlanner() {
        return ROLE_PLANNER.equals( globalAccess ) || isAdmin();
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equals( globalAccess );
    }

    /**
     * Test if this user can access the given plan uri.
     * @param uri the plan uri
     * @return true if user is authorized for that plan
     */
    public boolean isUser( String uri ) {
        return globalAccess != null
            || uri != null && planAccess.containsKey( uri );
    }

    /**
     * Test if this user can plan the given plan uri.
     * @param uri the plan uri
     * @return true if user is a planner for that plan
     */
    public boolean isPlanner( String uri ) {
        return isAdmin()
            || isPlanner()
            || uri != null && ROLE_PLANNER.equals( planAccess.get( uri ) );
    }

    /**
     * Test if a user has any access.
     * @return true if the user is authorized for at least a plan
     */
    public boolean isEnabled() {
        return globalAccess != null || !planAccess.isEmpty();
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
        if ( globalAccess != null ) {
            buffer.append( ',' );
            buffer.append( globalAccess );
        }

        return buffer.toString();
    }

    /**
     * Give a role to this user for all plans.
     * @param role either ROLE_ADMIN, ROLE_PLANNER, ROLE_USER or null.
     * Setting to null removes all authorities.
     */
    private void grantGlobalAccess( String role ) {
        globalAccess = null;

        if ( ROLE_USER.equals( role ) ) {
            globalAccess = role;
            for ( String uri : new HashSet<String>( planAccess.keySet() ) )
                if ( ROLE_USER.equals( planAccess.get( uri ) ) )
                    planAccess.remove( uri );

        } else {
            planAccess.clear();
            if ( ROLE_ADMIN.equals( role ) || ROLE_PLANNER.equals( role ) )
                globalAccess = role;
        }
    }

    /**
     * Grant proper authorities to a plan.
     * @see PlanManager#setAuthorities
     *
     * @param role either ROLE_ADMIN, ROLE_PLANNER, ROLE_USER or null for none
     * @param uri the plan's uri or null for all
     * @param planList available plans
     */
    void setAuthorities( String role, String uri, List<Plan> planList ) {
        if ( uri == null || ROLE_ADMIN.equals( role ) )
            grantGlobalAccess( role );

        else if ( ROLE_PLANNER.equals( role ) ) {
            if ( isPlanner() )
                grantOthers( planList, ROLE_PLANNER );
            else
                planAccess.put( uri, ROLE_PLANNER );

        } else if ( ROLE_USER.equals( role ) ) {
            if ( isUser() )
                grantOthers( planList, ROLE_USER );
            else
                planAccess.put( uri, ROLE_USER );

        } else {
            // role == null or other string
            if ( isPlanner() )
                grantOthers( planList, ROLE_PLANNER );
            else if ( isUser() )
                grantOthers( planList, ROLE_USER );
            planAccess.remove( uri );
        }
    }

    /**
     * Remove any specific access to a plan.
     * @param uri the plan uri
     */
    void clearAuthority( String uri ) {
        planAccess.remove( uri );       
    }

    private void grantOthers( List<Plan> planList, String role ) {
        for ( Plan plan : planList ) {
            String uri = plan.getUri();
            if ( !planAccess.containsKey( uri ) )
                planAccess.put( uri, role );
        }

        globalAccess = null;
    }
}
