package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;

/**
 * Remove a goal from a segment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 10, 2009
 * Time: 2:42:34 PM
 */
public class RemoveGoal extends AbstractCommand {

    public RemoveGoal() {
    }

    public RemoveGoal( Segment segment, Goal goal ) {
        needLockOn( segment );
        set( "segment", segment.getId() );
        set( "organization", goal.getOrganization().getName() );
        set( "category", goal.getCategory().name() );
        set( "positive", goal.isPositive() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "delete goal";
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
    public Change execute( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        boolean positive = (Boolean)get("positive");
        Goal.Category category = Goal.Category.valueOf( (String) get( "category" ) );
        Goal goal = segment.getGoal( category, positive, (String) get( "organization" ) );
        setTargetDescription( goal.getLabel() + " in segment " + segment.getName() );
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( segment, goal );
            set( "subCommands", multi );
        }
        // else this is a replay
        multi.execute( commander );
        return new Change( Change.Type.Recomposed, segment );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "undelete goal" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

    private MultiCommand makeSubCommands( Segment segment, Goal goal ) {
        MultiCommand subCommands = new MultiCommand( "delete goal - internal" );
        subCommands.addCommand( new UpdatePlanObject(
                segment,
                "goals",
                goal,
                UpdateObject.Action.Remove )
        );
        for ( Part part : segment.getAchievers( goal ) ) {
            subCommands.addCommand( new UpdateSegmentObject(
                    part,
                    "goals",
                    goal,
                    UpdateObject.Action.Remove
            ) );
        }
        return subCommands;
    }

}
