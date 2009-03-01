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
     * The type of command execution.
     */
    private Type type;

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
    
    /**
     * The type of a memento.
     */
    public enum Type {
        /**
         * Comand was executed.
         */
        Execute,
        /**
         * Command was an undo.
         */
        Undo,
        /**
         * Command was a redo.
         */
        Redo
    }

    public Memento( Type type, Command command ) {
        this.type = type;
        this.command = command;
        userName = Project.getUserName();
        date = new Date();
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
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
