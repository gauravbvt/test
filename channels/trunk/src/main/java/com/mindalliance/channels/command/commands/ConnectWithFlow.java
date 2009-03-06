package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Command to connect source to target with flow of given name.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 1:44:34 PM
 */
public class ConnectWithFlow extends AbstractCommand {


    public ConnectWithFlow( final Node source, final Node target, final String name ) {
        this( source, target, name, new HashMap<String, Object>() );
    }

    public ConnectWithFlow(
            final Node source,
            final Node target,
            final String name,
            final Map<String, Object> state ) {
        needLockOn( source );
        needLockOn( target );
        setArguments( new HashMap<String, Object>() {
            {
                put( "sourceScenario", source.getScenario().getId() );
                put( "source", source.getId() );
                put( "targetScenario", source.getScenario().getId() );
                put( "target", target.getId() );
                put( "name", name );
                put( "state", state );
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "connect"; 
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Flow execute( Service service ) throws CommandException {
        try {
            Scenario scenario = service.find(
                    Scenario.class,
                    (Long) getArgument( "sourceScenario" ) );
            Node source = scenario.getNode( (Long) getArgument( "source" ) );
            scenario = service.find(
                    Scenario.class,
                    (Long) getArgument( "targetScenario" ) );
            Node target = scenario.getNode( (Long) getArgument( "target" ) );
            String name = (String) getArgument( "name" );
            Flow flow = service.connect( source, target, name );
            Map<String, Object> state = (Map<String, Object>) getArgument( "state" );
            for ( String key : state.keySet() ) {
                setProperty( flow, key, state.get( key ) );
            }
            addArgument( "flow", flow.getId() );
            return flow;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Command makeUndoCommand( Service service ) throws CommandException {
        try {
            Scenario scenario = service.find( Scenario.class, (Long) getArgument( "scenario" ) );
            Long flowId = (Long) getArgument( "flow" );
            if ( flowId == null ) throw new CommandException( "Can't undo." );
            Flow flow = scenario.findFlow( flowId );
            return new BreakUpFlow( flow );
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo.", e );
        }
    }
}
