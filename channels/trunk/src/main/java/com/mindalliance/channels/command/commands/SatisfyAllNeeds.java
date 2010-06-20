package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;

import java.util.List;

/**
 * Satisfy as many of a part's needs as possible by creating flow from other parts with matching capabilities.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 22, 2009
 * Time: 6:44:30 PM
 */
public class SatisfyAllNeeds extends AbstractCommand {

    public SatisfyAllNeeds() {
    }

    public SatisfyAllNeeds( Part part ) {
        needLockOn( part );
        set( "segment", part.getSegment().getId() );
        set( "part", part.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "satify needs";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Commander commander ) {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Part part = (Part) segment.getNode( (Long) get( "part" ) );
            if ( part == null ) {
                return false;
            } else {
                List<Flow[]> satisfactions = commander.getQueryService().findUntappedSatisfactions( part );
                return !satisfactions.isEmpty();
            }
        } catch ( CommandException e ) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Part part = (Part) segment.getNode( (Long) get( "part" ) );
            if ( part == null ) throw new NotFoundException();
            MultiCommand multi = (MultiCommand) get( "subCommands" );
            if ( multi == null ) {
                multi = new MultiCommand( "satisfy needs - extra" );
                for ( Flow[] satisfaction :
                        commander.getQueryService().findUntappedSatisfactions( part ) ) {
                    // Keep need and capability even after capable and needy parts connected
                    multi.addCommand( new SatisfyNeed(
                            satisfaction[0],
                            satisfaction[1],
                            SatisfyNeed.KEEP_CAPABILITY,
                            SatisfyNeed.KEEP_NEED ) );
                }
                set( "subCommands", multi );
            }
            // else command replay
            multi.execute( commander );
            return new Change( Change.Type.Recomposed, part.getSegment() );
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
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "unsatisfy needs" );
        multi.setUndoes( getName() );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }


}
