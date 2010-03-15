// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.dao;

import com.mindalliance.mindpeer.model.User;

/**
 * User DAO.
 */
public interface UserDao  extends Dao<User> {

    /**
     * Find a user given its name.
     * @param name the name
     * @return the user or null if not found
     */
    User findByName( String name );

    /**
     * Find a user given its email address.
     * @param email the email of the user
     * @return the user or null if not found
     */
    User findByEmail( String email );

    /**
     * Find the id of the current user.
     * @return null if not authenticated
     */
    Long currentUserId();

}
