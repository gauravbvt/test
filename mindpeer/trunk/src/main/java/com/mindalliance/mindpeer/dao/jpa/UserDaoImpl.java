// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.dao.jpa;

import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * User DAO JPA implementation.
 */
public class UserDaoImpl extends AbstractDaoImpl<User> implements UserDao, UserDetailsService {

    /**
     * Create a new UserDaoImpl instance.
     */
    public UserDaoImpl() {
        super( User.class );
    }

    /**
     * Find a user given its name.
     * @param name the name
     * @return the user or null if not found
     */
    public User findByName( final String name ) {
        if ( name == null )
            return null;

        return (User) getJpaTemplate().execute( new JpaCallback() {
            public Object doInJpa( EntityManager em ) {
                Query query = em.createQuery(
                        "select e from User e where username = '" + name.toLowerCase() + '\'' );
                List<?> resultList = query.getResultList();
                return resultList.isEmpty() ? null : resultList.get( 0 );
            }
        } );
    }

    /**
     * Find a user given its email address.
     * @param email the email of the user
     * @return the user or null if not found
     */
    public User findByEmail( final String email ) {
        if ( email == null )
            return null;

        return (User) getJpaTemplate().execute( new JpaCallback() {
            public Object doInJpa( EntityManager em ) {
                Query query = em.createQuery(
                        "select e from User e where email = '" + email + '\'' );
                List<?> resultList = query.getResultList();
                return resultList.isEmpty() ? null : resultList.get( 0 );
            }
        } );
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search may possibly
     * be case insensitive, or case insensitive depending on how the implementaion instance is
     * configured. In this case, the <code>UserDetails</code> object that comes back may have a
     * username that is of a different case than what was actually requested..
     *
     * @param username the username presented to the
     * {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider}
     *
     * @return a fully populated user record (never <code>null</code>)
     */
    public UserDetails loadUserByUsername( String username ) {
        User user = findByName( username );
        if ( user == null )
            throw new UsernameNotFoundException( "User not found" );

        return user;
    }

    /**
     * Find current user.
     * @return null if not authenticated
     */
    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication != null ) {
            Object principal = authentication.getPrincipal();
            if ( principal instanceof User )
                return (User) principal;
        }

        return null;
    }

}
