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
import com.mindalliance.channels.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

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
        Service service = commander.getService();
        Importer importer = Project.getProject().getImporter();
        String xml = (String) get( "xml" );
        if ( xml != null ) {
            try {
                Long defaultScenarioId = (Long) get( "defaultScenario" );
                Scenario defaultScenario = null;
                if ( defaultScenarioId != null ) {
                    // a default scenario was added before removing the one to be restored.
                    List<Scenario> scenarios = service.list( Scenario.class );
                    assert scenarios.size() == 1;
                    defaultScenario = scenarios.get( 0 );
                    commander.mapId( defaultScenarioId, defaultScenario.getId() );
                }
                Scenario scenario = importer.importScenario(
                        new ByteArrayInputStream( xml.getBytes() ) );
                commander.mapId( (Long) get( "scenario" ), scenario.getId() );
                addArgument( "scenario", scenario.getId() );
                if ( defaultScenario != null ) service.remove( defaultScenario );
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
            Scenario scenario = commander.resolve(
                    Scenario.class,
                    (Long) get( "scenario" ) );
            return new RemoveScenario( scenario );
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo.", e );
        }
    }


}
