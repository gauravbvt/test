// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.impl;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A persistence controller for user managers.
 *
 * @see UserManager
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface UserManagerDAO {

    /**
     * Save a user manager to the persistent store.
     * @param manager the user manager to persist
     * @param output where to save
     * @throws IOException on errors
     */
    void save( UserManager manager, OutputStream output ) throws IOException;

}
