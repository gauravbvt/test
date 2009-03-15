package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.Part;

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

    public ConnectWithFlow() {
        super();
    }

    public ConnectWithFlow( final Node source, final Node target, final String name ) {
        this( source, target, name, new HashMap<String, Object>() );
    }

    public ConnectWithFlow(
            final Node source,
            final Node target,
            final String name,
            final Map<String, Object> state ) {
        super();
        needLockOn( source );
        needLockOn( target );
        final Part part;
        final Node other;
        final boolean isOutcome;
        if ( source.isPart() ) {
            isOutcome = true;
            part = (Part) source;
            other = target;
        } else {
            isOutcome = false;
            part = (Part) target;
            other = source;
        }
        setArguments( new HashMap<String, Object>() {
            {
                put( "isOutcome", isOutcome );
                put( "scenario", part.getScenario().getId() );
                put( "part", part.getId() );
                put( "otherScenario", other.getScenario().getId() );
                put( "other", other.getId() );
                put( "name", name );
                put( "attributes", state );
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
    public Change execute( Commander commander ) throws CommandException {
        Service service = commander.getService();
        try {
            Scenario scenario = service.find(
                    Scenario.class,
                    (Long) get( "scenario" ) );
            Part part = (Part) scenario.getNode( (Long) get( "part" ) );
            Scenario otherScenario = service.find(
                    Scenario.class,
                    (Long) get( "otherScenario" ) );
            Long nodeId = (Long) get( "other" );
            Node other = ( nodeId != null )
                    ? otherScenario.getNode( nodeId )
                    : service.createConnector( otherScenario );
            String name = (String) get( "name" );
            boolean isOutcome = (Boolean) get( "isOutcome" );
            Flow flow = isOutcome
                    ? service.connect( part, other, name )
                    : service.connect( other, part, name );
            Map<String, Object> attributes = (Map<String, Object>) get( "attributes" );
            if ( attributes != null ) {
                for ( String key : attributes.keySet() ) {
                    setProperty( flow, key, attributes.get( key ) );
                }
            }
            addArgument( "flow", flow.getId() );
            return new Change( Change.Type.Added, flow );
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
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        Service service = commander.getService();
        try {
            Scenario scenario = service.find( Scenario.class, (Long) get( "scenario" ) );
            Long flowId = (Long) get( "flow" );
            if ( flowId == null ) throw new CommandException( "Can't undo." );
            Flow flow = scenario.findFlow( flowId );
            return new DisconnectFlow( flow );
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo.", e );
        }
    }
}
