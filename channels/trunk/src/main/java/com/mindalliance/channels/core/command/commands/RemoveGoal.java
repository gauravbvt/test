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
import com.mindalliance.channels.core.command.commands.UpdateObject.Action;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Goal.Category;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;

/**
 * Remove a goal from a segment.
 */
public class RemoveGoal extends AbstractCommand {

    public RemoveGoal() {
        super( "daemon" );
    }

    public RemoveGoal( String userName, Segment segment, Goal goal ) {
        super( userName );
        needLockOn( segment );
        set( "segment", segment.getId() );
        set( "organization", goal.getOrganization().getName() );
        set( "category", goal.getCategory().name() );
        set( "positive", goal.isPositive() );
    }

    @Override
    public String getName() {
        return "remove goal";
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        boolean positive = (Boolean) get( "positive" );
        Category category = Category.valueOf( (String) get( "category" ) );
        Goal goal = segment.getGoal( category, positive, (String) get( "organization" ) );
        setTargetDescription( goal.getLabel() + " in segment " + segment.getName() );
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( segment, goal );
            set( "subCommands", multi );
        }
        // else this is a replay
        multi.execute( commander );
        return new Change( Type.Recomposed, segment );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( getUserName(), "undelete goal" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

    private MultiCommand makeSubCommands( Segment segment, Goal goal ) {
        MultiCommand subCommands = new MultiCommand( getUserName(), "delete goal - internal" );
        subCommands.addCommand( new UpdateModelObject( getUserName(), segment, "goals", goal, Action.Remove ) );
        for ( Part part : segment.getAchievers( goal ) )
            subCommands.addCommand( new UpdateSegmentObject( getUserName(), part, "goals", goal, Action.Remove ) );
        return subCommands;
    }
}
