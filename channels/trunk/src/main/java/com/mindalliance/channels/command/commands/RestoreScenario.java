package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.NotFoundException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Restore a delete scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 9, 2009
 * Time: 11:22:35 AM
 */
public class RestoreScenario extends AbstractCommand {

    public RestoreScenario() {

    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "restore scenario";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Importer importer = Project.getProject().getImporter();
        String xml = (String) get( "xml" );
        if ( xml != null ) {
            try {
                Scenario scenario = importer.importScenario(
                        new ByteArrayInputStream( xml.getBytes() ) );
                addArgument( "scenario", scenario.getId() );
                return new Change( Change.Type.Added, scenario );
            } catch ( IOException e ) {
                throw new CommandException( "Can't restore scenario.", e );
            }
        } else {
            throw new CommandException( "Can't restore scenario." );
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
            Scenario scenario = commander.getService().find(
                    Scenario.class,
                    (Long) get( "scenario" ) );
            return new RemoveScenario( scenario );
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo.", e );
        }
    }


}
