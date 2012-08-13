package com.mindalliance.channels.core.model;

/**
 * Model entity is not of the expected kind (acutal instead of type, or vice-versa).
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 10, 2009
 * Time: 6:57:54 AM
 */
public class InvalidEntityKindException extends RuntimeException {
    public InvalidEntityKindException() {
    }

    public InvalidEntityKindException( String message ) {
        super( message );
    }

    public InvalidEntityKindException( String message, Throwable cause ) {
        super( message, cause );
    }

    public InvalidEntityKindException( Throwable cause ) {
        super( cause );
    }
}
