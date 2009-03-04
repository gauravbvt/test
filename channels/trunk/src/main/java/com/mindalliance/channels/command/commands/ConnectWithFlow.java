package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.pages.Project;

import java.util.HashMap;

/**
 * Command to connect source to target with flow of given name.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 1:44:34 PM
 */
//TODO - allow setting state of the flow
public class ConnectWithFlow extends AbstractCommand {

    public ConnectWithFlow( final Node source, final Node target, final String name ) {
        needLockOn( source );
        needLockOn( target );
        setArguments( new HashMap<String, Object>() {
            {
                put( "scenario", source.getScenario().getId() );
                put( "source", source.getId() );
                put( "target", target.getId() );
                put( "name", name );
            }
        } );
    }

    public String getName() {
        return "connect";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Flow execute() throws NotFoundException {
        Scenario scenario = Project.service().find( Scenario.class, (Long) getArgument( "scenario" ) );
        Node source = scenario.getNode( (Long) getArgument( "source" ) );
        Node target = scenario.getNode( (Long) getArgument( "target" ) );
        String name = (String) getArgument( "name" );
        Flow flow = Project.service().connect( source, target, name );
        addArgument( "flow", flow.getId() );
        return flow;
    }

    public boolean isUndoable() {
        return true;
    }

    public Command makeUndoCommand() throws CommandException {
        try {
            Scenario scenario = Project.service().find( Scenario.class, (Long) getArgument( "scenario" ) );
            Long flowId = (Long) getArgument( "flow" );
            if ( flowId == null ) throw new CommandException( "Can't undo." );
            Flow flow = scenario.findFlow( flowId );
            return new BreakUpFlow( flow );
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo." );
        }
    }
}
