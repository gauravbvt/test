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
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;

import java.util.List;

/**
 * Link flow's classifications after making them identical.
 */
public class LinkFlowClassifications extends AbstractCommand {

    public LinkFlowClassifications() {
        super( "daemon" );
    }

    public LinkFlowClassifications( String userName, Flow flow ) {
        super( userName );
        needLockOn( flow );
        set( "segment", flow.getSegment().getId() );
        set( "flow", flow.getId() );
    }

    @Override
    public String getName() {
        return "link element classifications";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            if ( flow == null )
                throw new NotFoundException();
            boolean sameClassifications = flow.areAllEOIClassificationsSame();
            MultiCommand multi = (MultiCommand) get( "subCommands" );
            if ( multi == null ) {
                multi = makeSubCommands( flow, sameClassifications );
                set( "subCommands", multi );
            }
            multi.execute( commander );
            return sameClassifications ?
                   new Change( Type.Updated, flow, "classificationsLinked" ) :
                   new Change( Type.Updated, flow, "eois" );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    private MultiCommand makeSubCommands( Flow flow, boolean sameClassifications ) {
        MultiCommand subCommands = new MultiCommand( getUserName(), "link classifications - extra" );
        subCommands.setMemorable( false );
        subCommands.addCommand( UpdateObject.makeCommand( getUserName(),
                                                          flow,
                                                          "classificationsLinked",
                                                          true,
                                                          Action.Set ) );
        if ( !sameClassifications ) {
            List<ElementOfInformation> newEOIs = flow.getEOISWithSameClassifications();
            subCommands.addCommand( UpdateObject.makeCommand( getUserName(), flow, "eois", newEOIs, Action.Set ) );
        }
        return subCommands;
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( getUserName(), "unlink element classifications" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }
}
