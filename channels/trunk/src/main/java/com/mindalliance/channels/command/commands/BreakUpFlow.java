package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Scenario;

import java.util.Map;

/**
 * Command to break up a given flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 7:21:58 PM
 */
public class BreakUpFlow extends AbstractCommand {

    public BreakUpFlow() {
    }

    public BreakUpFlow( Flow flow ) {
        needLocksOn( CommandUtils.getLockingSetFor( flow ) );
        set( "flow", flow.getId() );
        set( "scenario", flow.getScenario().getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Break up flow";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            Flow flow = scenario.findFlow( (Long) get( "flow" ) );
            MultiCommand multi = (MultiCommand) get( "subCommands" );
            if ( multi == null ) {
                multi = makeSubCommands( flow );
                set( "subCommands", multi );
            }
            // else this is a replay
            multi.execute( commander );
            set( "flowState", CommandUtils.getFlowState( flow ) );
            flow.disconnect();
//            breakup( flow, commander );
            ignoreLock( (Long) get( "flow" ) );
            return new Change( Change.Type.Recomposed, scenario );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
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
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "reconnect flow" );
        multi.setUndoes( getName() );
        ConnectWithFlow connectWithFlow = new ConnectWithFlow();
        connectWithFlow.setArguments( (Map<String, Object>) get( "flowState" ) );
        connectWithFlow.set( "flow", get( "flow" ) );
        multi.addCommand( connectWithFlow );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }
    // Create a capability and/or need if not repetitive
    private MultiCommand makeSubCommands( Flow flow ) {
        MultiCommand subCommands = new MultiCommand( "breakup flow - extra" );
        subCommands.setMemorable( false );
        if ( flow.isInternal() ) {
            Node source = flow.getSource();
            Node target = flow.getTarget();
            if ( !source.isConnector() && !target.isConnector() ) {
                if ( !source.hasMultipleOutcomes( getName() ) ) {
                    Command addCapability = new AddCapability();
                    addCapability.set( "scenario", source.getScenario().getId() );
                    addCapability.set( "part", source.getId() );
                    addCapability.set( "name", flow.getName() );
                    addCapability.set( "attributes", CommandUtils.getFlowAttributes( flow ) );
                    subCommands.addCommand( addCapability );
                }
                if ( !target.hasMultipleRequirements( getName() ) ) {
                    Command addNeed = new AddNeed();
                    addNeed.set( "scenario", target.getScenario().getId() );
                    addNeed.set( "part", target.getId() );
                    addNeed.set( "name", flow.getName() );
                    addNeed.set( "attributes", CommandUtils.getFlowAttributes( flow ) );
                    subCommands.addCommand( addNeed );
                }
            }
        }
        return subCommands;
    }

}
