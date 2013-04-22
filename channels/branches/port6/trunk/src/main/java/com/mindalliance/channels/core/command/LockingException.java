package com.mindalliance.channels.core.command;

/**
 * An exception raised when acquiring a lock fails.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 1, 2009
 * Time: 9:48:47 PM
 */
public class LockingException extends Exception {

    /**
     * {@inheritDoc}
     */
    public LockingException() {
    }

    /**
     * {@inheritDoc}
     */
    public LockingException( String message ) {
        super( message );
    }

    /**
     * {@inheritDoc}
     */
    public LockingException( String message, Throwable cause ) {
        super( message, cause );
    }

    /**
     * {@inheritDoc}
     */
    public LockingException( Throwable cause ) {
        super( cause );
    }
}
