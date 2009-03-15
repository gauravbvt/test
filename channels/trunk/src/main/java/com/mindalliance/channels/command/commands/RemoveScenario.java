package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.export.Exporter;

import java.util.Iterator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 6, 2009
 * Time: 8:59:52 AM
 */
public class RemoveScenario extends AbstractCommand {

    public RemoveScenario( Scenario scenario ) {
        super();
        addArgument( "scenario", scenario.getId() );
        addConflicting( scenario );
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            addConflicting( parts.next() );
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
        Service service = commander.getService();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            Scenario scenario = service.find( Scenario.class, (Long) get( "scenario" ) );
            Exporter exporter = Project.getProject().getExporter();
            exporter.exportScenario( scenario, bos );
            addArgument( "xml", bos.toString() );
            service.remove( scenario );
            return new Change( Change.Type.Removed, scenario );
        } catch ( NotFoundException e ) {
            throw new CommandException( "Failed to remove scenario.", e );
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
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        String xml = (String) get( "xml" );
        if ( xml != null ) {
            RestoreScenario restoreScenario = new RestoreScenario();
            restoreScenario.addArgument( "xml", xml );
            return restoreScenario;
        } else {
            throw new CommandException( "Can not restore scenario." );
        }
    }
}
