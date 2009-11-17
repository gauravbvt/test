package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;

import java.util.List;

/**
 * Disconnect a scenario's external flows and then remove it.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 17, 2009
 * Time: 11:35:49 AM
 */
public class DisconnectAndRemoveScenario extends AbstractCommand {

    public DisconnectAndRemoveScenario() {
    }

    @SuppressWarnings( "unchecked" )
    public DisconnectAndRemoveScenario( Scenario scenario ) {
        List<Part> internalParts = scenario.listParts();
        List<Flow> internalFlows = scenario.listFlows();
        List<Part> externalParts = scenario.listExternalParts();
        needLocksOn( internalParts );
        needLocksOn( internalFlows );
        needLocksOn( externalParts );
        addConflicting( externalParts );
        set( "scenario", scenario.getId() );
    }

    public String getName() {
        return "remove scenario";
    }

    public Change execute( Commander commander ) throws CommandException {
        Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = new MultiCommand( "remove scenario - extra" );
            for ( Flow externalFlow : scenario.listExternalFlows() ) {
                multi.addCommand( new DisconnectFlow( externalFlow ) );
            }
            multi.addCommand( new RemoveScenario( scenario ) );
        }
        set( "subCommands", multi );
        multi.execute( commander );
        return new Change( Change.Type.Removed, scenario );
    }

    public boolean isUndoable() {
        return true;
    }

    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "restore scenario" );
        multi.setUndoes( getName() );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isScenarioSpecific() {
        return false;
    }


}
