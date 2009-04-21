package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Part;

/**
 * Copy a flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 20, 2009
 * Time: 1:25:19 PM
 */
public class CopyFlow extends AbstractCommand {

    public CopyFlow( Flow flow, Part part ) {
        set( "part", part.getId() );
        set( "flow", flow.getId() );
        set( "scenario", flow.getScenario().getId() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMemorable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "copy flow";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
        Flow flow;
        Part part;
        try {
            part = (Part) scenario.getNode( commander.resolveId( (Long) get( "part" ) ) );
            flow = scenario.findFlow( commander.resolveId( (Long) get( "flow" ) ) );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
        commander.setCopy( flow.getTarget() == part 
                ? CommandUtils.getNeedState( flow, part )
                : CommandUtils.getCapabilityState( flow, part ));
        return new Change( Change.Type.None, flow );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        return null;
    }
}
