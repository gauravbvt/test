package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.export.Exporter;

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
        super();
        set( "scenario", scenario.getId() );
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
        DataQueryObject dqo = commander.getDqo();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Exporter exporter = Channels.instance().getExporter();
            exporter.exportScenario( scenario, bos );
            set( "xml", bos.toString() );
            if ( dqo.list( Scenario.class ).size() == 1 ) {
                // first create a new scenario
                Scenario defaultScenario = dqo.createScenario();
                set( "defaultScenario", defaultScenario.getId() );
            }
            dqo.remove( scenario );
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
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        String xml = (String) get( "xml" );
        if ( xml != null ) {
            RestoreScenario restoreScenario = new RestoreScenario();
            restoreScenario.set( "xml", xml );
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
