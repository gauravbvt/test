/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.ChannelsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Moving parts and their flows to another segment.
 */
public class MoveParts extends AbstractCommand {

    public MoveParts() {
        super( "daemon" );
    }

    public MoveParts( String userName, List<Part> parts, Segment fromSegment, Segment toSegment ) {
        super( userName );
        List<Long> partIds = new ArrayList<Long>();
        for ( Part part : parts ) {
            needLocksOn( ChannelsUtils.getLockingSetFor( part ) );
            partIds.add( part.getId() );
        }
        addConflicting( fromSegment );
        addConflicting( toSegment );
        set( "parts", partIds );
        set( "fromSegment", fromSegment.getId() );
        set( "toSegment", toSegment.getId() );
    }

    @Override
    public String getName() {
        return "move tasks";
    }

    @Override
    public boolean canDo( Commander commander ) {
        return super.canDo( commander ) && commander.getPlan().getSegmentCount() > 1;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        Segment fromSegment = commander.resolve( Segment.class, (Long) get( "fromSegment" ) );
        Segment toSegment = commander.resolve( Segment.class, (Long) get( "toSegment" ) );
        List<Part> parts = new ArrayList<Part>();
        for ( Long id : (List<Long>) get( "parts" ) ) {
            Part part = commander.resolve( Part.class, id );
            parts.add( part );
        }
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( parts, fromSegment, toSegment );
            set( "subCommands", multi );
        }
        multi.execute( commander );
        for ( Part part : parts )
            ignoreLocksOn( ChannelsUtils.getLockingSetFor( part ) );
        describeTarget( toSegment );
        return new Change( Change.Type.Recomposed, fromSegment );
    }

    private MultiCommand makeSubCommands( List<Part> parts, Segment fromSegment, Segment toSegment ) {
        MultiCommand subCommands = new MultiCommand( getUserName(), "move parts - extra" );
        // make sure there's always at least one part in a segment
        if ( fromSegment.countParts() == parts.size() ) {
            subCommands.addCommand( new AddPart( getUserName(),
                                                 fromSegment,
                                                 getUserName() ) );
        }
        for ( Part part : parts ) {
            subCommands.addCommand( new MovePart( getUserName(), part, toSegment ) );
        }
        return subCommands;
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( getUserName(), "unmove tasks" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }
}
