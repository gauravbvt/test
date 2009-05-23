package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Scenario;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 22, 2009
 * Time: 3:37:36 PM
 */
public class HelloCommand extends AbstractCommand {

    public HelloCommand() {
    }

    public HelloCommand( String greeting ) {
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
        Scenario scenario = commander.getQueryService().getDefaultScenario();
        command.addConflicting( scenario );
        return command;
    }


}

