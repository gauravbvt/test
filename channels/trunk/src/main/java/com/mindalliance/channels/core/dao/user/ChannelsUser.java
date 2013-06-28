package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.users.UserRecordService;
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

    public static final String ANONYMOUS_USERNAME = "(Anonymous)";

    /**
     * Persistent information from database.
     */
    private UserRecord userRecord;

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
    /**
     * URI of the the current plan community for this user.
     */
    private String planCommunityUri;

    //----------------------------------------
    public ChannelsUser() {
        anonymous = true;
        userRecord = new UserRecord( ANONYMOUS_USERNAME, ANONYMOUS_USERNAME );
    }


    public ChannelsUser( UserRecord userRecord ) {
        this.userRecord = userRecord;
        anonymous = false;
    }

    public ChannelsUser( UserRecord userRecord, CommunityService communityService ) {
        this( userRecord );
        planCommunityUri = communityService.getPlanCommunity().getUri();
        plan = communityService.getPlan();
    }

    /**
     * Returns the "best practices" i.e. collaboration plan.
     * @return a plan
     */
    public Plan getPlan() {
        return plan;
    }

    public void setPlan( Plan plan ) {
        this.plan = plan;
        if ( plan != null )
            planCommunityUri = null;
    }

    public void setCommunityService( CommunityService communityService ) {
        setPlan( communityService.getPlan() );
        setPlanCommunityUri( communityService.getPlanCommunity().getUri() );
    }

    public String getPlanCommunityUri() {
        return planCommunityUri;
    }

    public void setPlanCommunityUri( String planCommunityUri ) {
        this.planCommunityUri = planCommunityUri;
        if ( planCommunityUri != null )
            plan = null;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public UserRecord getUserRecord() {
        return userRecord;
    }

    //    @Secured( "ROLE_ADMIN" )
    public void setUserRecord( UserRecord userRecord ) {
        this.userRecord = userRecord;
    }

    @Override
    public String getUsername() {
        return userRecord.getUsername();
    }

    public String getEmail() {
        return userRecord.getEmail();
    }

    public String getFullName() {
        return userRecord.getFullName();
    }

    @Override
    public String getPassword() {
        return userRecord.getPassword();
    }

    @Override
    public boolean isEnabled() {
        return userRecord.isEnabled();
    }

    public boolean isAdmin() {
        return userRecord.isAdmin();
    }

    /**
     * Returns the authorities granted to the user. Cannot return {@code null}.
     *
     * @return the authorities (never {@code null})
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
        if ( userRecord.isAdmin() )
            result.add( new GrantedAuthorityImpl( UserRecord.ROLE_ADMIN ) );
        String uri = plan == null ? null : plan.getUri();
        if ( userRecord.isPlannerOrAdmin( uri ) )
            result.add( new GrantedAuthorityImpl( UserRecord.ROLE_PLANNER ) );
        if ( userRecord.isParticipant( uri ) )
            result.add( new GrantedAuthorityImpl( UserRecord.ROLE_USER ) );

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
    public static ChannelsUser current( UserRecordService userDao ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication != null ) {
            Object obj = authentication.getPrincipal();
            if ( obj instanceof ChannelsUser ) {
                ChannelsUser user = (ChannelsUser) obj;
                userDao.refresh( user.getUserRecord() );
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
        return userRecord.isParticipant( uri );
    }

    /**
     * Test if this user can modify the given plan.
     *
     * @param uri a plan uri
     * @return a boolean
     */
    public boolean isPlannerOrAdmin( String uri ) {
        return userRecord.isPlannerOrAdmin( uri );
    }

    /**
     * Test if this user is a community planner..
     *
     * @param communityUri a plan uri
     * @return a boolean
     */
    public boolean isCommunityPlanner( String communityUri ) {
        return userRecord.isCommunityPlanner( communityUri );
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
     * Return a normalized version of the full name without username.
     *
     * @return a string
     */
    public String getSimpleNormalizedFullName() {
        return getNormalizedFullName( false );
    }


    /**
     * Return a normalized version of the full name with or without username.
     *
     * @param withUsername whether to add username
     * @return a string
     */
    public String getNormalizedFullName( boolean withUsername ) {
        String normalized = normalizeFullName( getFullName() );
        if ( withUsername )
            return normalized + " (" + getUsername() + ')';
        else
            return normalized;
    }

    static public String normalizeFullName( String fullName ) {
        String normalized;
        String name = fullName.trim();
        int index = name.lastIndexOf( ' ' );
        if ( index >= 0 ) {
            String s = name.substring( 0, index );
            normalized = name.substring( index + 1 ) + ", " + s;
        } else {
            normalized = name;
        }
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
        return userRecord.isAdmin() ? ADMIN
                : userRecord.isPlannerOrAdmin( planUri ) ? PLANNER
                : userRecord.isParticipant( planUri ) ? PARTICIPANT
                : UNAUTHORIZED;
    }

    /**
     * Return a string describing the most privileged role of the user.
     *
     * @return a String
     */
    public String getRole( ) {
        String planUri = getPlanUri();
        return userRecord.isAdmin() ? ADMIN
                : userRecord.isPlannerOrAdmin( planUri ) ? PLANNER
                : userRecord.isParticipant( planUri ) ? PARTICIPANT
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

    public String getPhoto() {
        return userRecord.getPhoto();
    }

    public void setPhoto( String s ) {
        userRecord.setPhoto( s );
    }

    public static String contextUri() {
        return current().getContextUri();
    }

    private String getContextUri() {
        return plan != null
                ? plan.getUri()
                : planCommunityUri;
    }

}

