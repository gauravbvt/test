package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.Scenario;

/**
 * Command to add a scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 6, 2009
 * Time: 8:58:12 AM
 */
public class AddScenario extends AbstractCommand {

    public AddScenario() {
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "add scenario";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Scenario scenario = commander.getDqo().createScenario();
        commander.mapId( (Long)get("scenario"), scenario.getId() );
        commander.mapId( (Long)get("defaultPart"), scenario.getDefaultPart().getId() );
        addArgument( "scenario", scenario.getId() );
        addArgument( "defaultPart", scenario.getDefaultPart().getId() );
        return new Change( Change.Type.Added, scenario );
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
