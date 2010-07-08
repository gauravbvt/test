package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;

import java.util.List;

/**
 * Disconnect a segment's external flows and then remove it.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 17, 2009
 * Time: 11:35:49 AM
 */
public class DisconnectAndRemoveSegment extends AbstractCommand {

    public DisconnectAndRemoveSegment() {
    }

    @SuppressWarnings( "unchecked" )
    public DisconnectAndRemoveSegment( Segment segment ) {
        List<Part> internalParts = segment.listParts();
        List<Flow> internalFlows = segment.listFlows();
        List<Part> externalParts = segment.listExternalParts();
        needLocksOn( internalParts );
        needLocksOn( internalFlows );
        needLocksOn( externalParts );
        addConflicting( externalParts );
        set( "segment", segment.getId() );
    }

    public String getName() {
        return "remove this segment";
    }

    public Change execute( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        describeTarget( segment );                
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = new MultiCommand( "remove plan segment - extra" );
            for ( Flow externalFlow : segment.listExternalFlows() ) {
                multi.addCommand( new DisconnectFlow( externalFlow ) );
            }
            multi.addCommand( new RemoveSegment( segment ) );
        }
        set( "subCommands", multi );
        multi.execute( commander );
        return new Change( Change.Type.Removed, segment );
    }

    public boolean isUndoable() {
        return true;
    }

    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "restore plan segment" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSegmentSpecific() {
        return false;
    }


}
