package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.Change;

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
            Flow flow = scenario.findFlow( commander.resolveId( (Long) get( "flow" ) ) );
            if ( flow == null ) throw new NotFoundException();
            boolean isOutcome = (Boolean) get( "outcome" );
            duplicate = duplicate( flow, isOutcome );
            commander.mapId( (Long) get( "duplicate" ), duplicate.getId() );
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
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Long flowId = (Long) get( "duplicate" );
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

    /**
     * Make a duplicate of the flow
     *
     * @param flow      a flow to duplicate
     * @param isOutcome whether to replicate as outcome or requirement
     * @return a created flow
     */
    public Flow duplicate( Flow flow, boolean isOutcome ) {
        Flow duplicate;
        if ( isOutcome ) {
            Node source = flow.getSource();
            Scenario scenario = flow.getSource().getScenario();
            DataQueryObject dqo = scenario.getDqo();
            duplicate = dqo.connect( source, dqo.createConnector( scenario ), getName() );
        } else {
            Node target = flow.getTarget();
            Scenario scenario = target.getScenario();
            DataQueryObject dqo = scenario.getDqo();
            duplicate = dqo.connect( dqo.createConnector( scenario ), target, getName() );
        }
        duplicate.initFrom( flow );
        return duplicate;
    }


}
