package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.model.Flow;

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
        return commander.isFlowCopied();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        DataQueryObject dqo = commander.getDqo();
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Part part = (Part) scenario.getNode( commander.resolveId( (Long) get( "part" ) ) );
            if ( part == null ) throw new NotFoundException();
            Map<String, Object> flowState;
            if ( commander.isReplaying() ) {
                flowState = (Map<String, Object>) get( "copy" );
            } else {
                flowState = commander.getCopy();
                set( "copy", flowState );
            }
            boolean isOutcome = (Boolean) flowState.get( "isOutcome" );
            Flow flow;
            if ( isOutcome ) {
                flow = dqo.connect(
                        part,
                        dqo.createConnector( scenario ),
                        (String) flowState.get( "name" ) );
            } else {
                flow = dqo.connect(
                        dqo.createConnector( scenario ),
                        part,
                        (String) flowState.get( "name" ) );
            }
            Map<String, Object> flowAttributes = (Map<String, Object>) flowState.get( "attributes" );
            if ( flowAttributes != null ) {
                CommandUtils.initialize( flow, flowAttributes );
            }
            if ( get( "flow" ) != null ) {
                commander.mapId( (Long) get( "flow" ), flow.getId() );
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
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Long flowId = (Long) get( "flow" );
            if ( flowId == null ) {
                throw new CommandException( "Can't undo." );
            } else {
                Flow flow = scenario.findFlow( commander.resolveId( flowId ) );
                return new DisconnectFlow( flow );
            }
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo", e );
        }
    }

}
