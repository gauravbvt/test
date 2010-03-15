// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.dao.jpa;

import com.mindalliance.mindpeer.dao.ProfileDao;
import com.mindalliance.mindpeer.model.Profile;
import org.springframework.orm.jpa.JpaCallback;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * ...
 */
public class ProfileDaoImpl extends AbstractDaoImpl<Profile> implements ProfileDao {

    public ProfileDaoImpl() {
        super( Profile.class );
    }

    /**
     * Find the profile of a user.
     * @param username the username
     * @return the profile, or null if not found
     */
    public Profile findByName( final String username ) {
        if ( username == null )
            return null;

        return (Profile) getJpaTemplate().execute( new JpaCallback() {
            public Object doInJpa( EntityManager em ) {
                Query query = em.createQuery(
                        "select p from Profile p join p.user u where u.username = :n" );
                query.setParameter( "n", username );
                List<?> resultList = query.getResultList();
                return resultList.isEmpty() ? null : resultList.get( 0 );
            }
        } );
    }
}
