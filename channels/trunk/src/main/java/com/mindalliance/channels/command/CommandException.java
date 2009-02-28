package com.mindalliance.channels.command;

/**
 * An exception caused by the definition or execution of a command.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 28, 2009
 * Time: 2:31:39 PM
 */
public class CommandException extends RuntimeException {
    /**
     * {@inheritDoc}
     */
    public CommandException() {
    }

    /**
     * {@inheritDoc}
     */
    public CommandException( String message ) {
        super( message );
    }

    /**
     * {@inheritDoc}
     */
    public CommandException( String message, Throwable cause ) {
        super( message, cause );
    }

    /**
     * {@inheritDoc}
     */
    public CommandException( Throwable cause ) {
        super( cause );
    }
}
