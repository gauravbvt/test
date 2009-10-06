package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Scenario;

/**
 * Satisfy a need by connecting with a capability.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 9:15:10 PM
 */
public class SatisfyNeed extends AbstractCommand {

    public SatisfyNeed() {
    }

    public SatisfyNeed( Flow need, Flow capability ) {
        needLocksOn( CommandUtils.getLockingSetFor( need ) );
        needLocksOn( CommandUtils.getLockingSetFor( capability ) );
        set( "needScenario", need.getScenario().getId() );
        set( "need", need.getId() );
        set( "capabilityScenario", capability.getScenario().getId() );
        set( "capability", capability.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "satisfy need";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Flow newFlow;
        QueryService queryService = commander.getQueryService();
        try {
            Scenario needScenario = commander.resolve( Scenario.class, (Long) get( "needScenario" ) );
            Flow need = needScenario.findFlow( (Long) get( "need" ) );
            Scenario capabilityScenario = commander.resolve(
                    Scenario.class,
                    (Long) get( "capabilityScenario" ) );
            Flow capability = capabilityScenario.findFlow( (Long) get( "capability" ) );
            Node fromNode;
            Node toNode;
            if ( needScenario == capabilityScenario ) {
                // Internal - from capability's part to need's part
                fromNode = capability.getSource();
                toNode = need.getTarget();
            } else {
                // Create flow in need's scenario
                     fromNode = capability.getTarget();
                    toNode = need.getTarget();
            }
            Long priorId = (Long) get( "satisfy" );
            newFlow = queryService.connect( fromNode, toNode, need.getName(), priorId );
            newFlow.setSignificanceToSource( capability.getSignificanceToSource() );
            newFlow.setSignificanceToTarget( need.getSignificanceToTarget() );
            newFlow.setChannels( need.isAskedFor() ? capability.getChannels() : need.getChannels() );
            newFlow.setMaxDelay( need.getMaxDelay() );
            set( "satisfy", newFlow.getId() );
            set( "context", newFlow.getScenario().getId() );
            MultiCommand multi = (MultiCommand) get( "subCommands" );
            if ( multi == null ) {
                multi = new MultiCommand( "satisfy need - extra" );
                if ( needScenario == capabilityScenario ) {
                    multi.addCommand( new RemoveCapability( capability ) );
                    multi.addCommand( new RemoveNeed( need ) );
                }
                 set( "subCommands", multi );
            }
            multi.execute( commander );
            return new Change( Change.Type.Added, newFlow );
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
        try {
            MultiCommand multi = new MultiCommand( "unsatisfy need" );
            multi.setUndoes( getName() );
            MultiCommand subCommands = (MultiCommand) get( "subCommands" );
            subCommands.setMemorable( false );
            multi.addCommand( subCommands.getUndoCommand( commander ) );
            // Disconnect need satisfying flow
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "context" ) );
            Flow newFlow = scenario.findFlow( (Long) get( "satisfy" ) );
            multi.addCommand( new DisconnectFlow( newFlow ) );
            return multi;
        } catch ( NotFoundException e ) {
            throw new CommandException("You need to refresh.", e);
        }
    }

}
