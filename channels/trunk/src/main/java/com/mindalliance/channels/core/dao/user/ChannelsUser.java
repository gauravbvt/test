package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.model.Plan;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/7/12
 * Time: 1:55 PM
 */

public class ChannelsUser implements UserDetails {

    public static final String ADMIN = "Admin";
    public static final String PLANNER = "Planner";
    public static final String PARTICIPANT = "Participant";
    public static final String UNAUTHORIZED = "Unauthorized";
    
    public static final String DEFAULT_ADMIN_USERNAME = "admin";
    public static final String DEFAULT_ADMIN_PASSWORD = "admin";

    private static final ChannelsUser ANONYMOUS = new ChannelsUser();

    /**
     * Persistent information from database.
     */
    private ChannelsUserInfo userInfo;

    /**
     * True if user is anonymous.
     */
    private final boolean anonymous;

    /**
     * Current plan for this user.
     */
    private Plan plan;
    /**
     * Web client info.
     */
    private WebClientInfo clientInfo;

    //----------------------------------------
    public ChannelsUser() {
        anonymous = true;
        userInfo = new ChannelsUserInfo( "(Anonymous)", "bla,Anonymous,bla" );
    }


    public ChannelsUser( ChannelsUserInfo userInfo ) {
        this.userInfo = userInfo;
        anonymous = false;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan( Plan plan ) {
        this.plan = plan;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public ChannelsUserInfo getUserInfo() {
        return userInfo;
    }

    //    @Secured( "ROLE_ADMIN" )
    public void setUserInfo( ChannelsUserInfo userInfo ) {
        this.userInfo = userInfo;
    }

    @Override
    public String getUsername() {
        return userInfo.getUsername();
    }

    public String getEmail() {
        return userInfo.getEmail();
    }

    public String getFullName() {
        return userInfo.getFullName();
    }

    @Override
    public String getPassword() {
        return userInfo.getPassword();
    }

    @Override
    public boolean isEnabled() {
        return userInfo.isEnabled();
    }

    public boolean isAdmin() {
        return userInfo.isAdmin();
    }

    /**
     * Returns the authorities granted to the user. Cannot return {@code null}.
     *
     * @return the authorities (never {@code null})
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
        if ( userInfo.isAdmin() )
            result.add( new GrantedAuthorityImpl( ChannelsUserInfo.ROLE_ADMIN ) );
        String uri = plan == null ? null : plan.getUri();
        if ( userInfo.isAPlanner( uri ) )
            result.add( new GrantedAuthorityImpl( ChannelsUserInfo.ROLE_PLANNER ) );
        if ( userInfo.isAUser( uri ) )
            result.add( new GrantedAuthorityImpl( ChannelsUserInfo.ROLE_USER ) );

        return Collections.unmodifiableList( result );
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot be authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired), {@code false}
     *         if no longer valid (ie expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
     *
     * @return <code>true</code> if the user is not locked, {@code false} otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired credentials prevent
     * authentication.
     *
     * @return {@code true} if the user's credentials are valid (ie non-expired),
     *         {@code false} if no longer valid (ie expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Get the current user of this session.
     *
     * @param userDao user dao
     * @return a user called "Anonymous", if not authenticated.
     */
    // TODO minimize uses of direct calls to User.current(...). Use DI...
    public static ChannelsUser current( ChannelsUserDao userDao ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication != null ) {
            Object obj = authentication.getPrincipal();
            if ( obj instanceof ChannelsUser ) {
                ChannelsUser user = (ChannelsUser) obj;
                userDao.refresh( user.getUserInfo() );
                return user;
            }
        }

        // return new ChannelsUser();
        return ANONYMOUS;
    }

    /**
     * Get the current user of this session.
     *
     * @return a user called "Anonymous", if not authenticated.
     */
    // TODO minimize uses of direct calls to User.current(...). Use DI...
    public static ChannelsUser current(  ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication != null ) {
            Object obj = authentication.getPrincipal();
            if ( obj instanceof ChannelsUser ) {
                ChannelsUser user = (ChannelsUser) obj;
                return user;
            }
        }

        // return new ChannelsUser();
        return ANONYMOUS;
    }

    /**
     * Test if this user can at least view the given plan.
     *
     * @param uri a plan uri
     * @return a boolean
     */
    public boolean isParticipant( String uri ) {
        return userInfo.isUser( uri );
    }

    /**
     * Test if this user can modify the given plan.
     *
     * @param uri a plan uri
     * @return a boolean
     */
    public boolean isPlanner( String uri ) {
        return userInfo.isPlanner( uri );
    }

    /**
     * Whether user is planner for current plan.
     *
     * @return a boolean
     */
    public boolean isPlanner() {
        return isPlanner( getPlan().getUri() );
    }

    @Override
    public String toString() {
        return getUsername();
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null || !getClass().isAssignableFrom( obj.getClass() ) )
            return false;

        ChannelsUser user = (ChannelsUser) obj;
        return anonymous == user.isAnonymous()
                && getUsername().equals( user.getUsername() );
    }

    @Override
    public int hashCode() {
        int result = getUsername().hashCode();
        result = 31 * result + ( anonymous ? 1 : 0 );
        return result;
    }

    /**
     * Return a normalized version of the full name with username.
     *
     * @return a string
     */
    public String getNormalizedFullName() {
        return getNormalizedFullName( true );
    }

    /**
     * Return a normalized version of the full name with or without username.
     *
     * @param withUsername whether to add username
     * @return a string
     */
    public String getNormalizedFullName( boolean withUsername ) {
        String normalized;
        String name = getFullName().trim();
        int index = name.lastIndexOf( ' ' );
        if ( index >= 0 ) {
            String s = name.substring( 0, index );
            normalized = name.substring( index + 1 ) + ", " + s;
        } else {
            normalized = name;
        }
        if ( withUsername )
            return normalized + " (" + getUsername() + ')';
        else
            return normalized;
    }


    /**
     * Test for a given role string in an authentication object.
     *
     * @param authentication the authentication
     * @param role           the role
     * @return true is role is included
     */
    public static boolean containsRole( Authentication authentication, String role ) {
        for ( GrantedAuthority a : authentication.getAuthorities() )
            if ( role.equals( a.getAuthority() ) )
                return true;

        return false;
    }

    /**
     * Return a string describing the most privileged role of the user.
     *
     * @param planUri the plan uri
     * @return a String
     */
    public String getRole( String planUri ) {
        return userInfo.isAdmin() ? ADMIN
                : userInfo.isPlanner( planUri ) ? PLANNER
                : userInfo.isUser( planUri ) ? PARTICIPANT
                : UNAUTHORIZED;
    }

    /**
     * Return a string describing the most privileged role of the user.
     *
     * @return a String
     */
    public String getRole( ) {
        String planUri = getPlanUri();
        return userInfo.isAdmin() ? ADMIN
                : userInfo.isPlanner( planUri ) ? PLANNER
                : userInfo.isUser( planUri ) ? PARTICIPANT
                : UNAUTHORIZED;
    }

    /**
     * Get uri of user's current plan.
     *
     * @return a string or null
     */
    public String getPlanUri() {
        return plan != null ? plan.getUri() : null;
    }

    /**
     * Get current plan from current thread.
     *
     * @return a plan
     */
    public static Plan plan() {
//        if ( Thread.currentThread() instanceof IssueScanner.Daemon ) {
//            return ( (IssueScanner.Daemon) Thread.currentThread() ).getPlan();
//        } else {
        ChannelsUser user = current();
        return user == null ? null : user.getPlan();
//        }
    }

    public WebClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo( WebClientInfo clientInfo ) {
        this.clientInfo = clientInfo;
    }

}

