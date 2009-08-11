package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.export.ImportExportFactory;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 6, 2009
 * Time: 8:59:52 AM
 */
public class RemoveScenario extends AbstractCommand {

    public RemoveScenario() {
    }

    public RemoveScenario( Scenario scenario ) {
        needLocksOn( scenario.listParts() );
        set( "scenario", scenario.getId() );
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            addConflicting( parts.next() );
        }
        Iterator<Flow> flows = scenario.flows();
        while ( flows.hasNext() ) {
            addConflicting( flows.next() );
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "remove scenario";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            scenario.setBeingDeleted( true );
            ImportExportFactory factory = Channels.instance().getImportExportFactory();
            Exporter exporter = factory.createExporter( queryService, commander.getPlan() );
            exporter.export( scenario, bos );
            set( "xml", bos.toString() );
            Plan plan = commander.getPlan();
            if ( plan.getScenarioCount() == 1 ) {
                // first create a new, replacement scenario
                Scenario defaultScenario = queryService.createScenario();
                plan.addScenario( defaultScenario );
                set( "defaultScenario", defaultScenario.getId() );
            }
            queryService.remove( scenario );
            return new Change( Change.Type.Removed, scenario );
        } catch ( IOException e ) {
            throw new CommandException( "Failed to remove scenario.", e );
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
        String xml = (String) get( "xml" );
        if ( xml != null ) {
            RestoreScenario restoreScenario = new RestoreScenario();
            restoreScenario.set( "xml", xml );
            Long defaultScenarioId = (Long) get( "defaultScenario" );
            if ( defaultScenarioId != null ) {
                restoreScenario.set( "defaultScenario", defaultScenarioId );
            }
            return restoreScenario;
        } else {
            throw new CommandException( "Can not restore scenario." );
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isScenarioSpecific() {
        return false;
    }
}
