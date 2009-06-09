package com.mindalliance.channels.model;

import com.mindalliance.channels.util.LoginFilter;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * A user of the system.
 */
@Entity
public class User {

    /** The user id of the user. */
    private String name = "(Anonymous)";

    /** Its full name. */
    private String fullName;

    /** True if user is anonymous. */
    private final boolean anonymous;

    /** Its email address. */
    private String email;

    /** True if user is a modeler. */
    private boolean modeler;

    public User() {
        anonymous = true;
    }

    public User( String name ) {
        this.name = name;
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
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Transient
    public boolean isAnonymous() {
        return anonymous;
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
            String name = obj instanceof UserDetails ? ( (UserDetails) obj ).getUsername()
                                              : obj.toString();
            User result = new User( name );

            if ( LoginFilter.containsRole( authentication, "ROLE_PLANNER" ) )
                result.setModeler( true );

            return result;

        } else
            return new User();
    }

    private void setModeler( boolean modeler ) {
        this.modeler = modeler;
    }

    public boolean isModeler() {
        return modeler;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name ;
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
            && name.equals( user.getName() );
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + ( anonymous ? 1 : 0 );
        return result;
    }
}
