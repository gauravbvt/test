// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

/**
 * Generic exception raised by models.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class ModelException extends Exception {

    /**
     * Create a new model exception.
     */
    public ModelException() {
        super();
    }

    /**
     * Create a new model exception.
     * @param message the message
     * @param cause the underlying cause
     */
    public ModelException( String message, Throwable cause ) {
        super( message, cause );
    }

    /**
     * Create a new model exception.
     * @param message the message
     */
    public ModelException( String message ) {
        super( message );
    }

    /**
     * Create a new model exception.
     * @param cause the underlying cause
     */
    public ModelException( Throwable cause ) {
        super( cause );
    }

}
