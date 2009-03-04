package com.mindalliance.channels.command;

import com.mindalliance.channels.pages.Project;

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
     * The name of the user who executed the command.
     */
    private String userName;
    /**
     * When the command was executed.
     */
    private Date date;

     public Memento() {
    }

    public Memento( Command command ) {
        this.command = command;
        userName = Project.getUserName();
        date = new Date();
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName( String userName ) {
        this.userName = userName;
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
}
