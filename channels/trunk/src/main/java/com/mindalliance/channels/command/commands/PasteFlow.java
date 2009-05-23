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
 * Paste copied flow into edited part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 20, 2009
 * Time: 4:06:16 PM
 */
public class PasteFlow extends AbstractCommand {

    public PasteFlow() {
    }

    public PasteFlow( Part part ) {
        needLockOn( part );
        set( "scenario", part.getScenario().getId() );
        set( "part", part.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "paste flow";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Commander commander ) {
        return super.canDo( commander) && commander.isFlowCopied();
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
            Map<String, Object> copy;
            copy = (Map<String, Object>) get( "copy" );
            if ( copy == null ) {
                // not replaying or redoing
                copy = commander.getCopy();
                set( "copy", copy );
            }
           Long priorId = (Long) get( "flow" );
            boolean isOutcome = (Boolean) copy.get( "isOutcome" );
            Flow flow;
            if ( isOutcome ) {
                flow = queryService.connect(
                        part,
                        queryService.createConnector( scenario ),
                        (String) copy.get( "name" ),
                        priorId );
            } else {
                flow = queryService.connect(
                        queryService.createConnector( scenario ),
                        part,
                        (String) copy.get( "name" ),
                        priorId );
            }
            Map<String, Object> flowAttributes = (Map<String, Object>) copy.get( "attributes" );
            if ( flowAttributes != null ) {
                CommandUtils.initialize( flow, flowAttributes );
                commander.getAttachmentManager().reattachAll( flow.getAttachmentTickets() );
            }
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
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Long flowId = (Long) get( "flow" );
            if ( flowId == null ) {
                throw new CommandException( "Can't undo." );
            } else {
                Flow flow = scenario.findFlow( flowId );
                return new DisconnectFlow( flow );
            }
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo", e );
        }
    }

}
