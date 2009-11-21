package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.util.ChannelsUtils;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 4:43:29 PM
 */
public class DuplicateFlow extends AbstractCommand {

    public DuplicateFlow() {
    }

    public DuplicateFlow( Flow flow, boolean isOutcome ) {
        needLockOn( isOutcome ? flow.getSource() : flow.getTarget() );
        set( "scenario", flow.getScenario().getId() );
        set( "flow", flow.getId() );
        set( "outcome", isOutcome );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "duplicate flow";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Flow duplicate;
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Flow flow = scenario.findFlow( (Long) get( "flow" ) );
            if ( flow == null ) throw new NotFoundException();
            boolean isOutcome = (Boolean) get( "outcome" );
            duplicate = ChannelsUtils.duplicate( flow, isOutcome, (Long) get( "duplicate" ) );
            set( "duplicate", duplicate.getId() );
            return new Change( Change.Type.Added, duplicate );
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
            Long flowId = (Long) get( "duplicate" );
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
