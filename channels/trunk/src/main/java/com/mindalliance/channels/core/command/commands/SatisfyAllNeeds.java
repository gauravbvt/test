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
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;

import java.util.List;

/**
 * Satisfy as many of a part's needs as possible by creating flow from other parts with matching capabilities.
 */
public class SatisfyAllNeeds extends AbstractCommand {

    public SatisfyAllNeeds() {
        super( "daemon" );
    }

    public SatisfyAllNeeds( String userName, Part part ) {
        super( userName );
        needLockOn( part );
        set( "segment", part.getSegment().getId() );
        set( "part", part.getId() );
    }

    @Override
    public String getName() {
        return "satisfy info needs";
    }

    @Override
    public boolean canDo( Commander commander ) {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Part part = (Part) segment.getNode( (Long) get( "part" ) );
            if ( part == null || !segment.isModifiabledBy( getUserName(), commander.getCommunityService() ) )
                return false;
            else {
                List<Flow[]> satisfactions = commander.getQueryService().findUntappedSatisfactions( part );
                return !satisfactions.isEmpty();
            }
        } catch ( CommandException e ) {
            return false;
        }
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Part part = (Part) segment.getNode( (Long) get( "part" ) );
            if ( part == null )
                throw new NotFoundException();
            MultiCommand multi = (MultiCommand) get( "subCommands" );
            if ( multi == null ) {
                multi = new MultiCommand( getUserName(), "satisfy needs - extra" );
                for ( Flow[] satisfaction : commander.getQueryService().findUntappedSatisfactions( part ) ) {
                    // Keep need and capability even after capable and needy parts connected
                    multi.addCommand( new SatisfyNeed( getUserName(),
                                                       satisfaction[0],
                                                       satisfaction[1],
                                                       SatisfyNeed.KEEP_CAPABILITY,
                                                       SatisfyNeed.KEEP_NEED ) );
                }
                set( "subCommands", multi );
            }
            // else command replay
            multi.execute( commander );
            describeTarget( part );
            return new Change( Type.Recomposed, part.getSegment() );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( getUserName(), "unsatisfy needs" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }
}
