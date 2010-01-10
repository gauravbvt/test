package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Scenario;

import java.util.List;
import java.util.Map;

/**
 * Paste copied part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 20, 2009
 * Time: 3:14:01 PM
 */
public class PastePart extends AbstractCommand {

    public PastePart() {
    }

    public PastePart( Scenario scenario ) {
        set( "scenario", scenario.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "paste task";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Commander commander ) {
        return super.canDo( commander ) && commander.isPartCopied();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
        Map<String, Object> copy;
        copy = (Map<String, Object>) get( "copy" );
        if ( copy == null ) {
            // not replaying or redoing
            copy = commander.getCopy();
            set( "copy", copy );
        }
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( copy );
            set( "subCommands", multi );
        }
        // else this is a replay
        multi.execute( commander );
        return new Change( Change.Type.Recomposed, scenario );
    }

    @SuppressWarnings( "unchecked" )
    private MultiCommand makeSubCommands( Map<String, Object> copy ) {
        MultiCommand subCommands = new MultiCommand( "paste part - extra" );
        subCommands.setMemorable( false );
        Command addPart = new AddPart();
        addPart.set( "scenario", get( "scenario" ) );
        addPart.set( "part", get( "part" ) );
        addPart.set( "partState", copy.get( "partState" ) );
        subCommands.addCommand( addPart );
        List<Map<String, Object>> needStates = (List<Map<String, Object>>) copy.get( "needs" );
        for ( Map<String, Object> needState : needStates ) {
            Command addNeed = new AddNeed();
            addNeed.set( "scenario", get( "scenario" ) );
            addNeed.set("name", needState.get("name"));
            addNeed.set( "attributes", needState.get( "attributes" ) );
            subCommands.addCommand( addNeed );
            subCommands.addLink( addPart, "id", addNeed, "part" );
        }
        List<Map<String, Object>> capabilityStates =
                (List<Map<String, Object>>) copy.get( "capabilities" );
        for ( Map<String, Object> capabilityState : capabilityStates ) {
            Command addCapability = new AddCapability();
            addCapability.set( "scenario", get( "scenario" ) );
            addCapability.set("name", capabilityState.get("name"));
            addCapability.set( "attributes", capabilityState.get( "attributes" ) );
            subCommands.addCommand( addCapability );
            subCommands.addLink( addPart, "id", addCapability, "part" );
        }
        return subCommands;
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
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "unpaste part" );
        multi.setUndoes( getName() );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

}
