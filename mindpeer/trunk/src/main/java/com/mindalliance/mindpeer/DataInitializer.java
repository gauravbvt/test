// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer;

import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * Initializer for an empty database.
 */
public class DataInitializer implements InitializingBean {

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
     * Called after all properties have been initialized.
     * @throws Exception on errors
     */
    @Transactional
    public void afterPropertiesSet() throws Exception {
        if ( userDao.countAll() == 0 ) {
            User admin = new User();
            admin.setUsername( "support" );
            admin.setPassword( "52357ffc0a5ed1f7fb909060187249b3c278a7da" );
            admin.setEmail( "support@mind-alliance.com" );
            admin.setConfirmed( true );
            userDao.save( admin );

            User guest = new User();
            guest.setUsername( "guest" );
            guest.setPassword( "da39a3ee5e6b4b0d3255bfef95601890afd80709" );
            guest.setEmail( "guest@example.com" );
            guest.setConfirmed( true );
            userDao.save( guest );
        }
    }
}
