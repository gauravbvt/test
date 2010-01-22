// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer;

import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import org.springframework.security.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;

/**
 * Initializer for an empty database.
 */
public class DataInitializer {

    private UserDao userDao;

    /**
     * Create a new DataInitializer instance.
     */
    public DataInitializer() {
    }

    /**
     * Sets the userDao of this DataInitializer.
     * @param userDao the userDao used for creating initial users.
     */
    public void setUserDao( UserDao userDao ) {
        this.userDao = userDao;
    }

    /**
     * Add some default users.
     */
    @Transactional
    @Secured( "RUN_AS_SYSTEM" )
    public void dataInit() {

        if ( userDao.countAll() == 0 ) {
            User admin = new User();
            admin.setUsername( "denis" );
            admin.setPassword( "cfbd5ca29de6baeaa5e340da5c618e932500b748" );
            admin.setEmail( "denis@mind-alliance.com" );
            admin.setConfirmation( null );
            userDao.save( admin );

            User guest = new User();
            guest.setUsername( "guest" );
            guest.setPassword( "da39a3ee5e6b4b0d3255bfef95601890afd80709" );
            guest.setEmail( "guest@example.com" );
            guest.setConfirmation( null );
            userDao.save( guest );

        }
    }
}
