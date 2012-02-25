package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.hibernate.search.annotations.Indexed;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/7/12
 * Time: 2:19 PM
 */
@Entity
@Indexed
@Table( uniqueConstraints = @UniqueConstraint( columnNames = {"EMAIL", "USERNAME"} ) )
public class ChannelsUserInfo extends AbstractPersistentPlanObject {

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

    /** The email. */
    private String email = "";

    /** The password. */
    private String password;

    /** The fullName. */
    private String fullName = "";

    /** The user's access (ROLE_USER|ROLE_PLANNER|ROLE_ADMIN), indexed by plan uri. */
    @Transient
    private Map<String,String> planAccess = new HashMap<String, String>();
    /**
     * Plan accesses as string.
     */
    private String planAccesses;

    /** The user's global access role (ROLE_USER|ROLE_PLANNER|ROLE_ADMIN|null). */
    private String globalAccess;

    //---------------------------------

    public ChannelsUserInfo() {}

    public ChannelsUserInfo( String username, String values ) {
        super( null, username );
        StringTokenizer tokens = new StringTokenizer( values, "," );

        password = tokens.nextToken();
        if ( tokens.hasMoreTokens() ) fullName = tokens.nextToken();
        if ( tokens.hasMoreTokens() ) email = tokens.nextToken();
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
        processPlanAccesses();
    }

    public ChannelsUserInfo( String username, String fullName, String email ) {
        super( null, username );
        this.fullName = fullName;
        this.email = email == null ? "" : email;
        processPlanAccesses();
    }

    /**
     * Remove redundant declarations.
     */
    private void processPlanAccesses() {
        if ( ROLE_ADMIN.equals( globalAccess ) || ROLE_PLANNER.equals( globalAccess ) ) {
            planAccess.clear();
        } else if ( ROLE_USER.equals( globalAccess ) ) {
            for ( String uri : new HashSet<String>( planAccess.keySet() ) )
                if ( ROLE_USER.equals( planAccess.get( uri ) ) )
                    planAccess.remove( uri );
        }
        planAccesses = planAccessesToString();
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public void setEmail( String email ) {
        if ( ChannelsUtils.isValidEmailAddress( email ) )
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

    public String getPlanAccesses() {
        return planAccesses;
    }

    public void setPlanAccesses( String planAccesses ) {
        this.planAccesses = planAccesses;
    }

    public String getGlobalAccess() {
        return globalAccess;
    }

    public void setGlobalAccess( String globalAccess ) {
        this.globalAccess = globalAccess;
    }

    /**
     * Set the password.
     * @param password unencrypted.
     */
    public void setPassword( String password ) {
        if ( password != null
                && !password.trim().isEmpty() )  {
            this.password = digestPassword( password.trim() );
        }
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

        buffer.append( getPassword() );
        buffer.append( ',' );
        buffer.append( getFullName() );
        buffer.append( ',' );
        buffer.append( getEmail() );
        buffer.append( planAccessesToString() );
        if ( getGlobalAccess() != null ) {
            buffer.append( ',' );
            buffer.append( getGlobalAccess() );
        }

        return buffer.toString();
    }

    private String planAccessesToString() {
        StringBuilder buffer = new StringBuilder();
        for ( Map.Entry<String, String> access : planAccess.entrySet() ) {
            buffer.append( ",[" );
            buffer.append( access.getKey() );
            if ( access.getValue().equals( ROLE_PLANNER ) ) {
                buffer.append( '|' );
                buffer.append( ROLE_PLANNER );
            }
            buffer.append( ']' );
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
        planAccesses = planAccessesToString();
    }

    /**
     * Grant proper authorities to a plan.
     * @see com.mindalliance.channels.core.dao.PlanManagerImpl#setAuthorities
     *
     * @param role either ROLE_ADMIN, ROLE_PLANNER, ROLE_USER or null for none
     * @param uri the plan's uri or null for all
     * @param planList available plans
     */
    public void setAuthorities( String role, String uri, List<Plan> planList ) {
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
        planAccesses = planAccessesToString();
    }

    /**
     * Remove any specific access to a plan.
     * @param uri the plan uri
     */
    public void clearAuthority( String uri ) {
        planAccess.remove( uri );
        planAccesses = planAccessesToString();
    }

    private void grantOthers( List<Plan> planList, String role ) {
        for ( Plan plan : planList ) {
            String uri = plan.getUri();
            if ( !planAccess.containsKey( uri ) )
                planAccess.put( uri, role );
        }
        globalAccess = null;
        planAccesses = planAccessesToString();
    }

    static public String digestPassword( String password ) {
        MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder(
                "sha",
                true );
        return encoder.encodePassword(
                password,
                null );
    }


}
