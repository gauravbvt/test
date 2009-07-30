package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.User;

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
        Long priorId = (Long) get( "scenario" );
        Long priorDefaultPartId = (Long) get("defaultPart");
        Scenario scenario = commander.getQueryService().createScenario(
                priorId,
                priorDefaultPartId);
        User.current().getPlan().addScenario( scenario );
        set( "scenario", scenario.getId() );
        set( "defaultPart", scenario.getDefaultPart().getId() );
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
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        return null;
    }
}
