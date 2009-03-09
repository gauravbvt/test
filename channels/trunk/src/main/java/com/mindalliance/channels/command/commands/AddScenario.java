package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 6, 2009
 * Time: 8:58:12 AM
 */
public class AddScenario extends AbstractCommand {

    public AddScenario() {
        super();
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
    public Object execute( Commander commander ) throws CommandException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * {@inheritDoc}
     */
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
