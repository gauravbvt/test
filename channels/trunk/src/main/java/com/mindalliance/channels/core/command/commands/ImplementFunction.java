package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Information;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Objective;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add missing goals, info needs and capabilities to implement function (as much as possible).
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/23/13
 * Time: 8:45 PM
 */
public class ImplementFunction extends AbstractCommand {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ImplementFunction.class );


    public ImplementFunction() {
        super( "daemon" );
    }

    public ImplementFunction( String userName, Part part ) {
        super( userName );
        needLockOn( part );
        set( "segment", part.getSegment().getId() );
        set( "part", part.getId() );
    }

    @Override
    public String getName() {
        return "implement function";
    }


    @Override
    public boolean canDo( Commander commander ) {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Part part = (Part) segment.getNode( (Long) get( "part" ) );
            return super.canDo( commander ) &&
                    part != null
                    && part.getFunction() != null
                    && !part.getFunction().implementedBy( part, commander.getQueryService() )
                    && segment.isModifiabledBy( getUserName(), commander.getCommunityService() );
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
            Function function = part.getFunction();
            MultiCommand multi = (MultiCommand) get( "subCommands" );
            if ( multi == null ) {
                multi = new MultiCommand( getUserName(), "implement function - extra" );
                // add goals
                for ( Objective objective : function.allObjectivesNotImplementedBy( part, commander.getQueryService() ) ) {
                    Goal goal = objective.findMatchingGoal( segment );
                    if ( goal != null ) {
                        multi.addCommand( new UpdateSegmentObject(
                                getUserName(),
                                part,
                                "goals",
                                goal,
                                UpdateObject.Action.AddUnique
                        ) );
                    }
                }
                // add info needs
                for ( Information info : function.allInfoNeedsNotImplementedBy( part ) ) {
                    multi.addCommand( new AddNeed(
                            getUserName(),
                            part,
                            info
                    ) );
                }
                // add capability
                for ( Information info : function.allInfoAcquiredNotImplementedBy( part ) ) {
                    multi.addCommand( new AddCapability(
                            getUserName(),
                            part,
                            info
                    ) );
                }

                set( "subCommands", multi );
            }
            // else command replay
            multi.execute( commander );
            describeTarget( part );
            return new Change( Change.Type.Recomposed, part.getSegment() );
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
        MultiCommand multi = new MultiCommand( getUserName(), "un-implement function" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }




}
