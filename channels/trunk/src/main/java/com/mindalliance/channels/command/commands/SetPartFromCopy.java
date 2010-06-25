package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.util.ChannelsUtils;

import java.util.List;
import java.util.Map;

/**
 * Set the attributes of a part from those of a copied part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 22, 2009
 * Time: 12:20:39 PM
 */
public class SetPartFromCopy extends AbstractCommand {

    public SetPartFromCopy() {
    }

    public SetPartFromCopy( Part part ) {
        addConflicting( part );
        set( "part", part.getId() );
        set( "segment", part.getSegment().getId() );
        set( "state", ChannelsUtils.getPartCopy( part ) );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "set task from copy";
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
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Part part = (Part) segment.getNode( (Long) get( "part" ) );
        Map<String, Object> copy;
        copy = (Map<String, Object>) get( "partCopy" );
        if ( copy == null ) {
            // not replaying or redoing
            copy = commander.getCopy();
            set( "partCopy", copy );
        }
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( copy );
            set( "subCommands", multi );
        } // TODO - attachment TICKETS are also copied - problem
        part.initFromMap( (Map<String, Object>) copy.get( "partState" ), commander.getQueryService() );
        multi.execute( commander );
        return new Change( Change.Type.Recomposed, segment );
    }

    @SuppressWarnings( "unchecked" )
    private MultiCommand makeSubCommands( Map<String, Object> copy ) {
        MultiCommand subCommands = new MultiCommand( "set task from copy - extra" );
        subCommands.setMemorable( false );
        List<Map<String, Object>> needStates = (List<Map<String, Object>>) copy.get( "needs" );
        for ( Map<String, Object> needState : needStates ) {
            Command addNeed = new AddNeed();
            addNeed.set( "segment", get( "segment" ) );
            addNeed.set( "part", get( "part" ) );
            addNeed.set( "name", needState.get( "name" ) );
            addNeed.set( "attributes", needState.get( "attributes" ) );
            subCommands.addCommand( addNeed );
        }
        List<Map<String, Object>> capabilityStates =
                (List<Map<String, Object>>) copy.get( "capabilities" );
        for ( Map<String, Object> capabilityState : capabilityStates ) {
            Command addCapability = new AddCapability();
            addCapability.set( "segment", get( "segment" ) );
            addCapability.set( "part", get( "part" ) );
            addCapability.set( "name", capabilityState.get( "name" ) );
            addCapability.set( "attributes", capabilityState.get( "attributes" ) );
            subCommands.addCommand( addCapability );
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
        SetPartFromCopy reset = new SetPartFromCopy();
        reset.set( "part", get( "part" ) );
        reset.set( "segment", get( "segment" ) );
        reset.set( "partCopy", get( "state" ) );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        reset.set( "subCommands", subCommands.getUndoCommand( commander ) );
        return reset;
    }

}
