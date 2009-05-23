package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.Importer;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Scenario;

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
        QueryService queryService = commander.getQueryService();
        Importer importer = commander.getChannels().getImporter();
        String xml = (String) get( "xml" );
        if ( xml != null ) {
            Long defaultScenarioId = (Long) get( "defaultScenario" );
            Scenario defaultScenario = null;
            if ( defaultScenarioId != null ) {
                // a default scenario was added before removing the one to be restored.
                List<Scenario> scenarios = queryService.list( Scenario.class );
                assert scenarios.size() == 1;
                defaultScenario = scenarios.get( 0 );
            }
            Scenario scenario = importer.restoreScenario( xml );
            commander.getAttachmentManager().reattachAll( scenario.getAttachmentTickets() );
            set( "scenario", scenario.getId() );
            if ( defaultScenario != null ) queryService.remove( defaultScenario );
            return new Change( Change.Type.Added, scenario );
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
    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        Scenario scenario = commander.resolve(
                Scenario.class,
                (Long) get( "scenario" ) );
        return new RemoveScenario( scenario );
    }


}
