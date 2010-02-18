// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.services;

import com.mindalliance.mindpeer.model.User;

/**
 * The main MindPeer service API.
 */
public interface MindPeer {

    /**
     * Register a new user and send verification email.
     * @param user the new user. The confirmation number will be reset for security
     * @param path the URL used by the user to get to MindPeer, eg. (http://localhost:8080/ or
     * https://mindpeer.mind-alliance.com/...)  This makes sure the link sent in the confirmation
     * email is actually accessible by the new user. Notice the trailing slash...
     */
    void register( User user, String path );
}
