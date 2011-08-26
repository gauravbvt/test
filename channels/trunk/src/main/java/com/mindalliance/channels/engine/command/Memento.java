package com.mindalliance.channels.engine.command;

import java.util.Date;

/**
 * A record of the successful execution of a command.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 28, 2009
 * Time: 2:24:06 PM
 */
public class Memento {

    /**
     * The command.
     */
    private Command command;

    /**
     * When the command was executed.
     */
    private long timestamp;

    public Memento() {
    }

    public Memento( Command command ) {
        this.command = command;
        timestamp = System.currentTimeMillis();
    }


    public String getUserName() {
        return command.getUserName();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( long timestamp ) {
        this.timestamp = timestamp;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand( Command command ) {
        this.command = command;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( '[' );
        sb.append( command.getName() );
        sb.append( " by " );
        sb.append( getUserName() );
        sb.append( " on " );
        sb.append( new Date( timestamp ) );
        sb.append( ']' );
        return sb.toString();
    }
}
