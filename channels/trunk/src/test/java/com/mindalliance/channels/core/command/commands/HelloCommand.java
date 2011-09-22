package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Segment;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 22, 2009
 * Time: 3:37:36 PM
 */
public class HelloCommand extends AbstractCommand {

    public HelloCommand() {
        super( User.current().getUsername() );
    }

    public HelloCommand( String greeting ) {
        super( User.current().getUsername() );
        set( "greeting", greeting );
    }

    public String getName() {
        return "Hello";
    }

    public Change execute( Commander commander ) throws CommandException {
        System.out.println( get( "greeting" ) + "! says " + getUserName() );
        return new Change();
    }

    public boolean isUndoable() {
        return true;
    }

    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        return makeCommand( "not " + get( "greeting" ), commander );
    }

    static public AbstractCommand makeCommand( String greeting, Commander commander ) {
        AbstractCommand command = new HelloCommand( greeting );
        Segment segment = commander.getQueryService().getDefaultSegment();
        command.addConflicting( segment );
        return command;
    }


}

