// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

/**
 * Exception raised when trying to add a user with the same
 * user name as another already existing one.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class UserExistsException extends Exception {

    /**
     * Default constructor.
     * @param message a descriptive message
     */
    public UserExistsException( String message ) {
        super( message );
    }
}
