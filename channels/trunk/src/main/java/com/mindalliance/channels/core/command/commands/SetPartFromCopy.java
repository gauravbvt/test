/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Change.Type;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Set the attributes of a part from those of a copied part.
 */
public class SetPartFromCopy extends AbstractCommand {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( SetPartFromCopy.class );


    public SetPartFromCopy() {
        super( "daemon" );
    }

    public SetPartFromCopy( String userName ) {
        super( userName );
    }

    public SetPartFromCopy( String userName, Part part ) {
        this( userName );
        needLockOn( part );
        set( "part", part.getId() );
        set( "segment", part.getSegment().getId() );
        set( "state", ChannelsUtils.getPartCopy( part ) );
    }

    @Override
    public String getName() {
        return "set task from copy";
    }

    @Override
    public boolean canDo( Commander commander ) {
        Segment segment = null;
        try {
            segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        } catch( CommandException e ) {
            LOG.warn( "Segment not found", e );
            return false;
        }
        return super.canDo( commander )
                && commander.isPartCopied( getUserName() )
                && segment.isModifiabledBy( getUserName(), commander.getCommunityService() );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Part part = (Part) segment.getNode( (Long) get( "part" ) );
        Map<String, Object> copy = (Map<String, Object>) get( "partCopy" );
        if ( copy == null ) {
            // not replaying or redoing
            copy = commander.getCopy( getUserName() );
            set( "partCopy", copy );
        }
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( copy );
            set( "subCommands", multi );
        }
        part.initFromMap(
                ( Map<String, Object>)copy.get( "partState" ),
                commander.getCommunityService() );
        multi.execute( commander );
        describeTarget( part );
        return new Change( Type.Recomposed, segment );
    }

    @SuppressWarnings( "unchecked" )
    private MultiCommand makeSubCommands( Map<String, Object> copy ) {
        MultiCommand subCommands = new MultiCommand( getUserName(), "set task from copy - extra" );
        subCommands.setMemorable( false );
        List<Map<String, Object>> needStates = (List<Map<String, Object>>) copy.get( "needs" );
        for ( Map<String, Object> needState : needStates ) {
            Command addNeed = new AddNeed( getUserName() );
            addNeed.set( "segment", get( "segment" ) );
            addNeed.set( "part", get( "part" ) );
            addNeed.set( "name", needState.get( "name" ) );
            addNeed.set( "attributes", needState.get( "attributes" ) );
            subCommands.addCommand( addNeed );
        }
        List<Map<String, Object>> capabilityStates = (List<Map<String, Object>>) copy.get( "capabilities" );
        for ( Map<String, Object> capabilityState : capabilityStates ) {
            Command addCapability = new AddCapability( getUserName() );
            addCapability.set( "segment", get( "segment" ) );
            addCapability.set( "part", get( "part" ) );
            addCapability.set( "name", capabilityState.get( "name" ) );
            addCapability.set( "attributes", capabilityState.get( "attributes" ) );
            subCommands.addCommand( addCapability );
        }
        return subCommands;
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        SetPartFromCopy reset = new SetPartFromCopy( getUserName() );
        reset.set( "part", get( "part" ) );
        reset.set( "segment", get( "segment" ) );
        reset.set( "partCopy", get( "state" ) );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        reset.set( "subCommands", subCommands.getUndoCommand( commander ) );
        return reset;
    }
}
