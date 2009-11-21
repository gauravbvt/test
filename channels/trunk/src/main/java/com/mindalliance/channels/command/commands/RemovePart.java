package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.util.ChannelsUtils;

import java.util.Iterator;

/**
 * Command to remove a part from a scenario after taking a copy.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 5, 2009
 * Time: 1:40:31 PM
 */
public class RemovePart extends AbstractCommand {

    /**
     * Logger.
     */
    // private static final Logger LOG = LoggerFactory.getLogger( RemovePart.class );
    public RemovePart() {
    }

    public RemovePart( Part part ) {
        needLocksOn( ChannelsUtils.getLockingSetFor( part ) );
        set( "part", part.getId() );
        set( "scenario", part.getScenario().getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "cut part";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Commander commander ) {
        return super.canDo( commander ) && isNotDefaultPart( commander );
    }

    private boolean isNotDefaultPart( Commander commander ) {
        try {
            Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
            return scenario.countParts() > 1;
        } catch ( CommandException e ) {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
        Part part = (Part) scenario.getNode( (Long) get( "part" ) );
        set( "partState", ChannelsUtils.getPartState( part ) );
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( part );
            set( "subCommands", multi );
        }
        // else this is a replay
        multi.execute( commander );
        if ( scenario.countParts() == 1 ) {
            Part defaultPart = queryService.createPart( scenario );
            set( "defaultPart", defaultPart.getId() );
        }
        scenario.removeNode( part );
        commander.releaseAnyLockOn( part );
        ignoreLock( (Long) get( "part" ) );
        return new Change( Change.Type.Recomposed, scenario );
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
        MultiCommand multi = new MultiCommand( "add part" );
        multi.setUndoes( getName() );
        // Reconstitute part
        Scenario scenario = commander.resolve( Scenario.class, (Long) get( "scenario" ) );
        AddPart addPart = new AddPart( scenario );
        addPart.set( "part", get( "part" ) );
        if ( get( "defaultPart" ) != null ) {
            addPart.set( "defaultPart", get( "defaultPart" ) );
        }
        addPart.set( "partState", get( "partState" ) );
        multi.addCommand( addPart );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;

    }

    /**
     * Make multi command for adding capabilities and needs in the wake of the part's removal.
     *
     * @param part a part
     * @return a multi command
     */
    private MultiCommand makeSubCommands( Part part ) {
        MultiCommand subCommands = new MultiCommand( "cut part - extra" );
        subCommands.addCommand( new CopyPart( part ) );
        Iterator<Flow> ins = part.requirements();
        while ( ins.hasNext() ) {
            Flow in = ins.next();
            subCommands.addCommand( new DisconnectFlow( in ) );
            // If the node to be removed is a part,
            // preserve the outcome of the source the flow represents
            if ( in.isInternal()
                    && in.getSource().isPart()
                    && !in.getSource().hasMultipleOutcomes( in.getName() ) ) {
                Command addCapability = new AddCapability();
                addCapability.set( "scenario", in.getSource().getScenario().getId() );
                addCapability.set( "part", in.getSource().getId() );
                addCapability.set( "name", in.getName() );
                addCapability.set( "attributes", ChannelsUtils.getFlowAttributes( in ) );
                subCommands.addCommand( addCapability );
            }
        }
        Iterator<Flow> outs = part.outcomes();
        while ( outs.hasNext() ) {
            Flow out = outs.next();
            subCommands.addCommand( new DisconnectFlow( out ) );
            // If the node to be removed is a part,
            // preserve the outcome of the source the flow represents
            if ( out.isInternal()
                    && out.getTarget().isPart()
                    && !out.getSource().hasMultipleRequirements( out.getName() ) ) {
                Command addNeed = new AddNeed();
                addNeed.set( "scenario", out.getTarget().getScenario().getId() );
                addNeed.set( "part", out.getTarget().getId() );
                addNeed.set( "name", out.getName() );
                addNeed.set( "attributes", ChannelsUtils.getFlowAttributes( out ) );
                subCommands.addCommand( addNeed );
            }
        }
        return subCommands;

    }

}
