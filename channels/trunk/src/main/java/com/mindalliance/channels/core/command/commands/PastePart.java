package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.model.Segment;

import java.util.List;
import java.util.Map;

/**
 * Paste copied part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 20, 2009
 * Time: 3:14:01 PM
 */
public class PastePart extends AbstractCommand {

    public PastePart() {
    }

    public PastePart( Segment segment ) {
        set( "segment", segment.getId() );
        addConflicting( segment );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "paste task";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Commander commander ) {
        return super.canDo( commander ) && commander.isPartCopied();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Map<String, Object> copy;
        copy = (Map<String, Object>) get( "copy" );
        if ( copy == null ) {
            // not replaying or redoing
            copy = commander.getCopy();
            set( "copy", copy );
        }
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( copy );
            set( "subCommands", multi );
        }
        setTargetDescription( makeTargetDescription( copy, commander ) );
        // else this is a replay
        multi.execute( commander );
        return new Change( Change.Type.Recomposed, segment );
    }

    @SuppressWarnings( "unchecked" )
    private String makeTargetDescription( Map<String, Object> copy, Commander commander ) {
        StringBuilder sb = new StringBuilder();
        sb.append( '"' );
        sb.append( ( (Map<String, Object>) copy.get( "partState" ) ).get( "task" ) );
        sb.append( '"' );
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            sb.append( " in segment " );
            sb.append( '"' );
            sb.append( segment.getName() );
            sb.append( '"' );
        } catch ( CommandException e ) {
            // do nothing
        }
        return sb.toString();
    }

    @SuppressWarnings( "unchecked" )
    private MultiCommand makeSubCommands( Map<String, Object> copy ) {
        MultiCommand subCommands = new MultiCommand( "paste task - extra" );
        subCommands.setMemorable( false );
        Command addPart = new AddPart();
        addPart.set( "segment", get( "segment" ) );
        // addPart.set( "part", get( "part" ) );
        addPart.set( "partState", copy.get( "partState" ) );
        subCommands.addCommand( addPart );
        List<Map<String, Object>> needStates = (List<Map<String, Object>>) copy.get( "needs" );
        for ( Map<String, Object> needState : needStates ) {
            Command addNeed = new AddNeed();
            addNeed.set( "segment", get( "segment" ) );
            addNeed.set( "name", needState.get( "name" ) );
            addNeed.set( "attributes", needState.get( "attributes" ) );
            subCommands.addCommand( addNeed );
            subCommands.addLink( addPart, "id", addNeed, "part" );
        }
        List<Map<String, Object>> capabilityStates =
                (List<Map<String, Object>>) copy.get( "capabilities" );
        for ( Map<String, Object> capabilityState : capabilityStates ) {
            Command addCapability = new AddCapability();
            addCapability.set( "segment", get( "segment" ) );
            addCapability.set( "name", capabilityState.get( "name" ) );
            addCapability.set( "attributes", capabilityState.get( "attributes" ) );
            subCommands.addCommand( addCapability );
            subCommands.addLink( addPart, "id", addCapability, "part" );
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
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "unpaste part" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

}
