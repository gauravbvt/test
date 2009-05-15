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
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;

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
        set( "part", part.getId() );
        set( "scenario", part.getScenario().getId() );
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
        QueryService queryService = commander.getQueryService();
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Part part = (Part) scenario.getNode( commander.resolveId( (Long) get( "part" ) ) );
            if ( part == null ) throw new NotFoundException();
            Flow flow = queryService.connect(
                    part,
                    queryService.createConnector( scenario ),
                    (String) get( "name" ) );
            Map<String, Object> flowAttributes = (Map<String, Object>) get( "attributes" );
            if ( flowAttributes != null ) {
                CommandUtils.initialize( flow, flowAttributes );
                commander.getAttachmentManager().reattachAll( flow.getAttachmentTickets() );
            }
            commander.mapId( (Long)get("flow"), flow.getId() );
            set( "flow", flow.getId() );
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
