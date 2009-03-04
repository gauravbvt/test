package com.mindalliance.channels.command;

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
    private Date date;

     public Memento() {
    }

    public Memento( Command command ) {
        this.command = command;
        date = new Date();
    }


    public String getUserName() {
        return command.getUserName();
    }

    public Date getDate() {
        return date;
    }

    public void setDate( Date date ) {
        this.date = date;
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
        sb.append('[');
        sb.append(command.getName());
        sb.append(',');
        sb.append(getUserName());
        sb.append(',');
        sb.append(date);
        sb.append(']');
        return sb.toString();
    }
}
