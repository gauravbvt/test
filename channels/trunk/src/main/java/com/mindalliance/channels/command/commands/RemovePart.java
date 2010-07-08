package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.util.ChannelsUtils;

import java.util.Iterator;

/**
 * Command to remove a part from a segment after taking a copy.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 5, 2009
 * Time: 1:40:31 PM
 */
public class RemovePart extends AbstractCommand {

    /**
     * Logger.
     */
    // private static final Logger LOG = LoggerFactory.getLogger( RemovePart.class );
    public RemovePart() {
    }

    public RemovePart( Part part ) {
        needLocksOn( ChannelsUtils.getLockingSetFor( part ) );
        set( "part", part.getId() );
        set( "segment", part.getSegment().getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "cut task";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Commander commander ) {
        return super.canDo( commander ) && isNotDefaultPart( commander );
    }

    private boolean isNotDefaultPart( Commander commander ) {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            return segment.countParts() > 1;
        } catch ( CommandException e ) {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Part part = (Part) segment.getNode( (Long) get( "part" ) );
        describeTarget( part );
        set( "partState", part.mapState() );
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( part );
            set( "subCommands", multi );
        }
        // else this is a replay
        multi.execute( commander );
        if ( segment.countParts() == 1 ) {
            Part defaultPart = queryService.createPart( segment );
            set( "defaultPart", defaultPart.getId() );
        }
        commander.getPlanDao().removeNode( part, segment );
        commander.releaseAnyLockOn( part );
        ignoreLock( (Long) get( "part" ) );
        return new Change( Change.Type.Recomposed, segment );
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
        MultiCommand multi = new MultiCommand( "add new task" );
        // Reconstitute part
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        AddPart addPart = new AddPart( segment );
        addPart.set( "part", get( "part" ) );
        if ( get( "defaultPart" ) != null ) {
            addPart.set( "defaultPart", get( "defaultPart" ) );
        }
        addPart.set( "partState", get( "partState" ) );
        multi.addCommand( addPart );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;

    }

    /**
     * Make multi command for adding capabilities and needs in the wake of the part's removal.
     *
     * @param part a part
     * @return a multi command
     */
    private MultiCommand makeSubCommands( Part part ) {
        MultiCommand subCommands = new MultiCommand( "cut task - extra" );
        subCommands.addCommand( new CopyPart( part ) );
        Iterator<Flow> ins = part.receives();
        while ( ins.hasNext() ) {
            Flow in = ins.next();
            subCommands.addCommand( new DisconnectFlow( in ) );
            // If the node to be removed is a part,
            // preserve the send of the source the flow represents
            if ( in.isInternal()
                    && in.getSource().isPart()
                    && !in.getSource().hasMultipleSends( in.getName() ) ) {
                Command addCapability = new AddCapability();
                addCapability.set( "segment", in.getSource().getSegment().getId() );
                addCapability.set( "part", in.getSource().getId() );
                addCapability.set( "name", in.getName() );
                addCapability.set( "attributes", ChannelsUtils.getFlowAttributes( in ) );
                subCommands.addCommand( addCapability );
            }
        }
        Iterator<Flow> outs = part.sends();
        while ( outs.hasNext() ) {
            Flow out = outs.next();
            subCommands.addCommand( new DisconnectFlow( out ) );
            // If the node to be removed is a part,
            // preserve the send of the source the flow represents
            if ( out.isInternal()
                    && out.getTarget().isPart()
                    && !out.getSource().hasMultipleReceives( out.getName() ) ) {
                Command addNeed = new AddNeed();
                addNeed.set( "segment", out.getTarget().getSegment().getId() );
                addNeed.set( "part", out.getTarget().getId() );
                addNeed.set( "name", out.getName() );
                addNeed.set( "attributes", ChannelsUtils.getFlowAttributes( out ) );
                subCommands.addCommand( addNeed );
            }
        }
        return subCommands;

    }

}
