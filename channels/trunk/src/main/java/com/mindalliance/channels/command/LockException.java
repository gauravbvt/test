package com.mindalliance.channels.command;

/**
 * An exception raised when acquiring a lock fails.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 1, 2009
 * Time: 9:48:47 PM
 */
public class LockException extends Exception {

    /**
     * {@inheritDoc}
     */
    public LockException() {
    }

    /**
     * {@inheritDoc}
     */
    public LockException( String message ) {
        super( message );
    }

    /**
     * {@inheritDoc}
     */
    public LockException( String message, Throwable cause ) {
        super( message, cause );
    }

    /**
     * {@inheritDoc}
     */
    public LockException( Throwable cause ) {
        super( cause );
    }
}
