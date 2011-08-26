package com.mindalliance.channels.social;

import com.mindalliance.channels.engine.command.Change;
import com.mindalliance.channels.engine.command.Command;

/**
 * Command event.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 2, 2010
 * Time: 12:02:30 PM
 */
public class CommandEvent extends PlanningEvent {

    public enum Type {
        Done,
        Undone,
        Redone
    }

    private Command command;
    private Change change;
    private Type type;

    public CommandEvent( String planId ) {
        super( planId );
    }

    public CommandEvent( Type type, Command command, String planId ) {
        this( planId );
        this.type = type;
        this.command = command;
    }

    public CommandEvent( Type type, Command command, Change change, String planId ) {
        this( type, command, planId );
        this.change = change;
    }

    public String getUsername() {
        return command.getUserName();
    }

    public Command getCommand() {
        return command;
    }

    public Change getChange() {
        return change;
    }

    public Type getType() {
        return type;
    }

    public boolean isDone() {
        return type == Type.Done;
    }

    public boolean isUndone() {
        return type == Type.Undone;
    }

    public boolean isRedone() {
        return type == Type.Redone;
    }

    public boolean isCommandEvent() {
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "Command " );
        sb.append( command.getName() );
        if ( isDone() ) {
            sb.append( " executed on ");
            sb.append( change.getClassName() );
            sb.append( '[');
            sb.append( change.getId() );
            sb.append( ']' );
        } else {
            sb.append(type == Type.Undone ? " undone" : " done");
        }
        sb.append( " by ");
        sb.append( command.getUserName() );
        sb.append( " " );
        sb.append( super.toString() );
        return sb.toString();
    }
}
