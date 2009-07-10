package com.mindalliance.channels.model;

import com.mindalliance.channels.util.LoginFilter;
import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.annotation.Secured;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * A user of the system.
 */
@Entity
public class User implements UserDetails {

    /** The admin role string. */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    /** The planner role string. */
    public static final String ROLE_PLANNER = "ROLE_PLANNER";

    /** The user role string. Implied if user is listed in user list. */
    public static final String ROLE_USER = "ROLE_USER";

    public static final User ANONYMOUS = new User();

    /** The user id of the user. */
    private String username = "(Anonymous)";

    /** Its full name. */
    private String fullName;

    /** True if user is anonymous. */
    private final boolean anonymous;

    /** Its email address. */
    private String email;

    /** True if user is a modeler. */
    private boolean planner;

    /** True if user is an administrator. */
    private boolean admin;

    /** Encoded password checksum. */
    private String password;

    /** Current plan for this user. */
    private Plan plan;

    public User() {
        anonymous = true;
    }

    public User( String username ) {
        this.username = username;
        anonymous = false;
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

    @Id
    public String getUsername() {
        return username;
    }

    @Secured( { "ROLE_ADMIN", "RUN_AS_SERVER" } )
    public void setUsername( String username ) {
        this.username = username;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan( Plan plan ) {
        this.plan = plan;
    }

    /**
     * Switch to a new plan.
     * @param newPlan the plan
     * @return the previous plan
     */
    public Plan switchPlan( Plan newPlan ) {
        Plan result = plan;
        setPlan( newPlan );
        return result;
    }

    @Transient
    public boolean isAnonymous() {
        return anonymous;
    }

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities (never <code>null</code>)
     */
    @Transient
    public GrantedAuthority[] getAuthorities() {
        List<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
        if ( isAdmin() )
            result.add( new GrantedAuthorityImpl( ROLE_ADMIN ) );
        if ( isPlanner() )
            result.add( new GrantedAuthorityImpl( ROLE_PLANNER ) );

        result.add( new GrantedAuthorityImpl( ROLE_USER ) );

        return result.toArray( new GrantedAuthority[ result.size() ] );
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot be authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired), <code>false</code> if no longer valid
     *         (ie expired)
     */
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired credentials prevent
     * authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired), <code>false</code> if no longer
     *         valid (ie expired)
     */
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    @Transient
    public boolean isEnabled() {
        return true;
    }

    /**
     * Get the current user of this session.
     * @return a user called "Anonymous", if not authenticated.
     */
    // TODO minimize uses of direct calls to User.current(). Use DI...
    public static User current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication != null ) {
            Object obj = authentication.getPrincipal();
            User result = obj instanceof User ? (User) obj : new User( obj.toString() );

            if ( LoginFilter.containsRole( authentication, "ROLE_PLANNER" ) )
                result.setPlanner( true );

            if ( LoginFilter.containsRole( authentication, "ROLE_ADMIN" ) )
                result.setAdmin( true );

            return result;

        } else
            return ANONYMOUS;
    }

    @Secured( { "ROLE_ADMIN", "RUN_AS_SERVER" } )
    public void setPlanner( boolean planner ) {
        this.planner = planner;
    }

    public boolean isPlanner() {
        return planner;
    }

    public boolean isAdmin() {
        return admin;
    }

    @Secured( { "ROLE_ADMIN", "RUN_AS_SERVER" } )
    public void setAdmin( boolean admin ) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return username;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null || getClass() != obj.getClass() )
            return false;

        User user = (User) obj;
        return anonymous == user.isAnonymous()
            && username.equals( user.getUsername() );
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + ( anonymous ? 1 : 0 );
        return result;
    }

    /**
     * Create string value for property file.
     * @return a new string
     */
    public String propertyString() {
        StringWriter buffer = new StringWriter();
        buffer.write( getPassword() );
        buffer.write( "," );
        buffer.write( plan == null ? "-1" : Long.toString( plan.getId() ) );
        for ( GrantedAuthority a : getAuthorities() ) {
            buffer.write( "," );
            buffer.write( a.getAuthority() );
        }
        return buffer.toString();
    }

    /**
     * Add a role to this user.
     * @param roleString a role string
     */
    @Secured( { "ROLE_ADMIN", "RUN_AS_SERVER" } )
    public void addRole( String roleString ) {
        if ( ROLE_ADMIN.equals( roleString ) )
            setAdmin( true );
        else if ( ROLE_PLANNER.equals( roleString ) )
            setPlanner( true );
    }
}
