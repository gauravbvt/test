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
import com.mindalliance.channels.core.model.EOIsHolder;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;

import java.util.List;

/**
 * Link flow's classifications after making them identical.
 */
public class LinkClassifications extends AbstractCommand {

    public LinkClassifications() {
        super( "daemon" );
    }

    public LinkClassifications( String userName, EOIsHolder eoiHolder ) {
        super( userName );
        needLockOn( eoiHolder );
        if ( eoiHolder.isFlow() ) {
            set( "segment", ((Flow)eoiHolder).getSegment().getId() );
            set( "flow", eoiHolder.getId() );
        } else {
            assert eoiHolder instanceof ModelObject;
            set( "class", eoiHolder.getClass().getCanonicalName() );
            set( "object", eoiHolder.getId() );

        }
    }

    @Override
    public String getName() {
        return "link element classifications";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        try {
            EOIsHolder eoiHolder = getEOIHolder( commander );
            boolean sameClassifications = eoiHolder.areAllEOIClassificationsSame();
            MultiCommand multi = (MultiCommand) get( "subCommands" );
            if ( multi == null ) {
                multi = makeSubCommands( eoiHolder, sameClassifications );
                set( "subCommands", multi );
            }
            multi.execute( commander );
            return sameClassifications ?
                   new Change( Type.Updated, eoiHolder, "classificationsLinked" ) :
                   new Change( Type.Updated, eoiHolder, "eois" );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    private EOIsHolder getEOIHolder( Commander commander ) throws NotFoundException, CommandException {
        if ( getArguments().containsKey( "flow" ) ) {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            if ( flow == null )
                throw new NotFoundException();
            else
                return (EOIsHolder)flow;
        } else if ( getArguments().containsKey( "object" ) ) {
               return getEOIHolderPlanObject( commander );
        } else {
            throw new CommandException( "Not an EOI holder" );
        }
    }

    @SuppressWarnings( "unchecked" )
    private EOIsHolder getEOIHolderPlanObject( Commander commander ) throws CommandException {
        String className = (String) get( "class" );
        Class<? extends ModelObject> clazz = null;
        try {
            clazz = (Class<? extends ModelObject>) getClass().getClassLoader().loadClass( className );
        } catch ( ClassNotFoundException e ) {
            throw new CommandException( "Invalid class name", e );
        }
        return (EOIsHolder)commander.resolve( clazz, (Long) get( "object" ) );
    }


    private MultiCommand makeSubCommands( EOIsHolder eoiHolder, boolean sameClassifications ) throws CommandException {
        MultiCommand subCommands = new MultiCommand( getUserName(), "link classifications - extra" );
        subCommands.setMemorable( false );
        subCommands.addCommand( UpdateObject.makeCommand( getUserName(),
                eoiHolder,
                                                          "classificationsLinked",
                                                          true,
                                                          Action.Set ) );
        if ( !sameClassifications ) {
            List<ElementOfInformation> newEOIs = eoiHolder.getEOISWithSameClassifications();
            subCommands.addCommand( UpdateObject.makeCommand( getUserName(), eoiHolder, "eois", newEOIs, Action.Set ) );
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
