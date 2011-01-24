package com.mindalliance.channels.dao;

import com.mindalliance.channels.model.Plan;
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
 * A user of the system.
 */
public class User implements UserDetails {

    private static final User ANONYMOUS = new User();

    private static final long serialVersionUID = -5817912937367287500L;

    /**
     * Raw information from the property file.
     */
    private final UserInfo userInfo;

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
    public User() {
        anonymous = true;
        userInfo = new UserInfo( "(Anonymous)", "bla,Anonymous,bla" );
    }

    public User( UserInfo userInfo ) {
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

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getEmail() {
        return userInfo.getEmail();
    }

    public String getFullName() {
        return userInfo.getFullName();
    }

    public String getPassword() {
        return userInfo.getPassword();
    }

    public String getUsername() {
        return userInfo.getUsername();
    }

    public boolean isEnabled() {
        return userInfo.isEnabled();
    }

    public boolean isAdmin() {
        return userInfo.isAdmin();
    }

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities (never <code>null</code>)
     */
    public Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
        if ( userInfo.isAdmin() )
            result.add( new GrantedAuthorityImpl( UserInfo.ROLE_ADMIN ) );

        String uri = plan == null ? null : plan.getUri();
        if ( userInfo.isPlanner( uri ) )
            result.add( new GrantedAuthorityImpl( UserInfo.ROLE_PLANNER ) );
        if ( userInfo.isUser( uri ) )
            result.add( new GrantedAuthorityImpl( UserInfo.ROLE_USER ) );

        return Collections.unmodifiableList( result );
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot be authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired), <code>false</code>
     *         if no longer valid (ie expired)
     */
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired credentials prevent
     * authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired),
     *         <code>false</code> if no longer valid (ie expired)
     */
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Get the current user of this session.
     *
     * @return a user called "Anonymous", if not authenticated.
     */
    // TODO minimize uses of direct calls to User.current(). Use DI...
    public static User current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication != null ) {
            Object obj = authentication.getPrincipal();
            if ( obj instanceof User )
                return (User) obj;
        }

        // return new User();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getUsername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null || getClass() != obj.getClass() )
            return false;

        User user = (User) obj;
        return anonymous == user.isAnonymous()
                && getUsername().equals( user.getUsername() );
    }

    /**
     * {@inheritDoc}
     */
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
        String normalized;
        String name = getFullName().trim();
        int index = name.lastIndexOf( ' ' );
        if ( index >= 0 ) {
            String s = name.substring( 0, index );
            normalized = name.substring( index + 1 ) + ", " + s;
        } else {
            normalized = name;
        }
        return normalized + " (" + getUsername() + ')';
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
        return userInfo.isAdmin() ? "Admin"
                : userInfo.isPlanner( planUri ) ? "Planner"
                : userInfo.isUser( planUri ) ? "Participant"
                : "Unauthorized";
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
        User user = current();
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
