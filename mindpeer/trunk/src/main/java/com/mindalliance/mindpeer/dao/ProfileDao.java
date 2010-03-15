// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.dao;

import com.mindalliance.mindpeer.model.Profile;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data access object for profiles.
 */
@Transactional
public interface ProfileDao extends Dao<Profile> {

    /**
     * Find the profile of a user.
     * @param username the username
     * @return the profile, or null if not found
     */
    Profile findByName( String username );

}
