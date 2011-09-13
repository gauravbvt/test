package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;

import java.util.List;

/**
 * Link flow's classifications after making them identical.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 6, 2009
 * Time: 11:18:56 AM
 */
public class LinkFlowClassifications extends AbstractCommand {

    public LinkFlowClassifications() {
    }

    public LinkFlowClassifications( Flow flow ) {
        needLockOn( flow );
        set( "segment", flow.getSegment().getId() );
        set( "flow", flow.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "link element classifications";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            if ( flow == null ) throw new NotFoundException();
            boolean sameClassifications = flow.areAllEOIClassificationsSame();
            MultiCommand multi = (MultiCommand) get( "subCommands" );
            if ( multi == null ) {
                multi = makeSubCommands( flow, sameClassifications );
                set( "subCommands", multi );
            }
            multi.execute( commander );
            if (sameClassifications) {
                return new Change( Change.Type.Updated, flow, "classificationsLinked" );
            } else {
                return new Change( Change.Type.Updated, flow, "eois" );
            }

        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    private MultiCommand makeSubCommands( Flow flow, boolean sameClassifications ) {
        MultiCommand subCommands = new MultiCommand( "link classifications - extra" );
        subCommands.setMemorable( false );
        subCommands.addCommand( UpdateObject.makeCommand(
                flow,
                "classificationsLinked",
                true,
                UpdateObject.Action.Set ) );
        if ( !sameClassifications ) {
            List<ElementOfInformation> newEOIs = flow.getEOISWithSameClassifications();
            subCommands.addCommand( UpdateObject.makeCommand(
                    flow,
                    "eois",
                    newEOIs,
                    UpdateObject.Action.Set ) );
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
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "unlink element classifications" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

}
