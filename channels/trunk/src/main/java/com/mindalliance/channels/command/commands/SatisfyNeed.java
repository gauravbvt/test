package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.util.ChannelsUtils;

/**
 * Satisfy a need by connecting with a capability.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 9:15:10 PM
 */
public class SatisfyNeed extends AbstractCommand {
    /**
     * Indicates desire to keep need after connecting need and capability.
     */
    public static final boolean KEEP_NEED = true;
    /**
     * Indicates desire to keep capability after connecting need and capability.
     */
    public static final boolean KEEP_CAPABILITY = true;

    public SatisfyNeed() {
    }

    /**
     * Constructor.
     *
     * @param need           a flow
     * @param capability     a flow
     * @param keepCapability whether *not* to delete connected capability if both local
     * @param keepNeed       whether *not* to delete connected need if both local
     */
    public SatisfyNeed( Flow need, Flow capability, boolean keepCapability, boolean keepNeed ) {
        needLocksOn( ChannelsUtils.getLockingSetFor( need ) );
        needLocksOn( ChannelsUtils.getLockingSetFor( capability ) );
        set( "needSegment", need.getSegment().getId() );
        set( "need", need.getId() );
        set( "capabilitySegment", capability.getSegment().getId() );
        set( "capability", capability.getId() );
        set( "keepNeed", keepNeed );
        set( "keepCapability", keepCapability );
    }

    public SatisfyNeed( Flow need, Flow capability ) {
        this( need, capability, false, false );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "satisfy need";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Flow newFlow;
        QueryService queryService = commander.getQueryService();
        try {
            Segment needSegment = commander.resolve( Segment.class, (Long) get( "needSegment" ) );
            Flow need = needSegment.findFlow( (Long) get( "need" ) );
            Segment capabilitySegment = commander.resolve(
                    Segment.class,
                    (Long) get( "capabilitySegment" ) );
            Flow capability = capabilitySegment.findFlow( (Long) get( "capability" ) );
            boolean keepCapability = (Boolean) get( "keepCapability" );
            boolean keepNeed = (Boolean) get( "keepNeed" );
            Node fromNode;
            Node toNode;
            if ( needSegment == capabilitySegment ) {
                // Internal - from capability's part to need's part
                fromNode = capability.getSource();
                toNode = need.getTarget();
            } else {
                // Create flow in need's segment
                fromNode = capability.getTarget();
                toNode = need.getTarget();
            }
            Long priorId = (Long) get( "satisfy" );
            String name = need.getName().isEmpty() ? capability.getName() : need.getName();
            newFlow = queryService.connect( fromNode, toNode, name, priorId );
            newFlow.setEois( capability.copyEois() );
            newFlow.setSignificanceToSource( capability.getSignificanceToSource() );
            newFlow.setSignificanceToTarget( need.getSignificanceToTarget() );
            newFlow.setChannels( need.isAskedFor() ? capability.getChannels() : need.getChannels() );
            newFlow.setMaxDelay( need.getMaxDelay() );
            set( "satisfy", newFlow.getId() );
            set( "context", newFlow.getSegment().getId() );
            MultiCommand multi = (MultiCommand) get( "subCommands" );
            if ( multi == null ) {
                multi = new MultiCommand( "satisfy need - extra" );
                if ( needSegment.equals( capabilitySegment ) ) {
                    if ( !keepCapability ) {
                        multi.addCommand( new RemoveCapability( capability ) );
                    }
                    if ( !keepNeed ) {
                        multi.addCommand( new RemoveNeed( need ) );
                    }
                }
                set( "subCommands", multi );
            }
            multi.execute( commander );
            return new Change( Change.Type.Added, newFlow );
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
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        try {
            MultiCommand multi = new MultiCommand( "unsatisfy need" );
            multi.setUndoes( getName() );
            MultiCommand subCommands = (MultiCommand) get( "subCommands" );
            subCommands.setMemorable( false );
            multi.addCommand( subCommands.getUndoCommand( commander ) );
            // Disconnect need satisfying flow
            Segment segment = commander.resolve( Segment.class, (Long) get( "context" ) );
            Flow newFlow = segment.findFlow( (Long) get( "satisfy" ) );
            multi.addCommand( new DisconnectFlow( newFlow ) );
            return multi;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

}
