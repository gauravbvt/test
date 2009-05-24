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
 * Time: 9:01:03 AM
 */
public class AddNeed extends AbstractCommand {

    public AddNeed() {
    }

    public AddNeed( Part part ) {
        addConflicting( part );
        set( "part", part.getId() );
        set( "scenario", part.getScenario().getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "add information need";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Part part = (Part) scenario.getNode( (Long) get( "part" ) );
            if ( part == null ) throw new NotFoundException();
            Long priorId = (Long) get( "flow" );
            Flow flow = queryService.connect(
                    queryService.createConnector( scenario ),
                    part,
                    (String) get( "name" ),
                    priorId );
            set( "flow", flow.getId() );
            Map<String, Object> flowAttributes = (Map<String, Object>) get( "attributes" );
            if ( flowAttributes != null ) {
                CommandUtils.initialize( flow, flowAttributes );
                commander.getAttachmentManager().reattachAll( flow.getAttachmentTickets() );
            }
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
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Flow flow = scenario.findFlow( (Long) get( "flow" ) );
            return new RemoveNeed( flow );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
    }
}
