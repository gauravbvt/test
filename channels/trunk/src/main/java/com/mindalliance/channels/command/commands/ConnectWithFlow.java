package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.model.Part;

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
        Map<String, Object> args = new HashMap<String, Object>();
        args.put( "isOutcome", isOutcome );
        args.put( "scenario", part.getScenario().getId() );
        args.put( "part", part.getId() );
        args.put( "otherScenario", other.getScenario().getId() );
        args.put( "other", other.getId() );
        args.put( "name", name );
        args.put( "attributes", state );
        setArguments( args );
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
        DataQueryObject dqo = commander.getDqo();
        Scenario scenario = commander.resolve(
                Scenario.class,
                (Long) get( "scenario" ) );
        Part part = (Part) scenario.getNode( commander.resolveId( (Long) get( "part" ) ) );
        Scenario otherScenario = commander.resolve(
                Scenario.class,
                (Long) get( "otherScenario" ) );
        Long nodeId = commander.resolveId( (Long) get( "other" ) );
        Node other = CommandUtils.resolveNode( nodeId, otherScenario, dqo );
        String name = (String) get( "name" );
        boolean isOutcome = (Boolean) get( "isOutcome" );
        Flow flow = isOutcome
                ? dqo.connect( part, other, name )
                : dqo.connect( other, part, name );
        Map<String, Object> attributes = (Map<String, Object>) get( "attributes" );
        if ( attributes != null ) {
            CommandUtils.initialize( flow, attributes );
        }
        if ( get( "flow" ) != null )
            commander.mapId( (Long) get( "flow" ), flow.getId() );
        set( "flow", flow.getId() );
        return new Change( Change.Type.Added, flow );
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
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Long flowId = (Long) get( "flow" );
            if ( flowId == null ) throw new CommandException( "Can't undo." );
            Flow flow = scenario.findFlow( commander.resolveId( flowId ) );
            return new DisconnectFlow( flow );
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo.", e );
        }
    }
}
