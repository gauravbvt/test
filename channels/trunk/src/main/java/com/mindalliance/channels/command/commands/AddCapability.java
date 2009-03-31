package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.NotFoundException;

import java.util.Map;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 6, 2009
 * Time: 9:05:33 AM
 */
public class AddCapability extends AbstractCommand {

    public AddCapability() {
    }

    public AddCapability( Part part ) {
        addConflicting( part );
        addArgument( "part", part.getId() );
        addArgument( "scenario", part.getScenario().getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "add sharing capability";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        DataQueryObject dqo = commander.getDqo();
        Flow flow;
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Part part = (Part) scenario.getNode( commander.resolveId( (Long) get( "part" ) ) );
            if ( part == null ) throw new NotFoundException();
            flow = dqo.connect( part, dqo.createConnector( scenario ), "" );
            Map<String, Object> flowAttributes = (Map<String, Object>) get( "attributes" );
            if ( flowAttributes != null ) {
                CommandUtils.initialize( flow, flowAttributes );
            }
            commander.mapId( (Long)get("flow"), flow.getId() );
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
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Flow flow = scenario.findFlow( commander.resolveId( (Long) get( "flow" ) ) );
            return new RemoveCapability( flow );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
    }
}
