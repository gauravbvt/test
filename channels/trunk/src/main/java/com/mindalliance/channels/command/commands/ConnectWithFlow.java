package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;

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
        QueryService queryService = commander.getQueryService();
        Scenario scenario = commander.resolve(
                Scenario.class,
                (Long) get( "scenario" ) );
        Part part = (Part) scenario.getNode( (Long) get( "part" ) );
        Scenario otherScenario = commander.resolve(
                Scenario.class,
                (Long) get( "otherScenario" ) );
        Long nodeId =  (Long) get( "other" );
        Node other = CommandUtils.resolveNode( nodeId, otherScenario, queryService );
        String name = (String) get( "name" );
        boolean isOutcome = (Boolean) get( "isOutcome" );
        Long priorId = (Long) get( "flow" );
        Flow flow = isOutcome
                ? queryService.connect( part, other, name, priorId )
                : queryService.connect( other, part, name, priorId );
        Map<String, Object> attributes = (Map<String, Object>) get( "attributes" );
        if ( attributes != null ) {
            CommandUtils.initialize( flow, attributes );
        }
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
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Long flowId = (Long) get( "flow" );
            if ( flowId == null ) throw new CommandException( "Can't undo." );
            Flow flow = scenario.findFlow( flowId );
            return new DisconnectFlow( flow );
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo.", e );
        }
    }
}
