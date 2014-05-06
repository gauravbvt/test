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
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.time.Delay;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;

/**
 * Satisfy a need by connecting with a capability.
 */
public class SatisfyNeed extends AbstractCommand {

    /**
     * Indicates desire to keep capability after connecting need and capability.
     */
    public static final boolean KEEP_CAPABILITY = true;

    /**
     * Indicates desire to keep need after connecting need and capability.
     */
    public static final boolean KEEP_NEED = true;

    //-------------------------------
    public SatisfyNeed() {
        super( "daemon" );
    }

    /**
     * Constructor.
     *
     * @param userName the user executing the command
     * @param need a flow
     * @param capability a flow
     * @param keepCapability whether *not* to delete connected capability if both local
     * @param keepNeed whether *not* to delete connected need if both local
     */
    public SatisfyNeed( String userName, Flow need, Flow capability, boolean keepCapability, boolean keepNeed ) {
        super( userName );
        needLocksOn( ChannelsUtils.getLockingSetFor( need ) );
        needLocksOn( ChannelsUtils.getLockingSetFor( capability ) );
        set( "needSegment", need.getSegment().getId() );
        set( "need", need.getId() );
        set( "capabilitySegment", capability.getSegment().getId() );
        set( "capability", capability.getId() );
        set( "keepNeed", keepNeed );
        set( "keepCapability", keepCapability );
    }

    //-------------------------------
    @Override
    public Change execute( Commander commander ) throws CommandException {
        Flow newFlow;
        QueryService queryService = commander.getQueryService();
        try {
            Segment needSegment = commander.resolve( Segment.class, (Long) get( "needSegment" ) );
            Flow need = needSegment.findFlow( (Long) get( "need" ) );
            Segment capabilitySegment = commander.resolve( Segment.class, (Long) get( "capabilitySegment" ) );
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

            describeTarget( toNode );
            Long priorId = (Long) get( "satisfy" );
            String name = need.getName().isEmpty() ? capability.getName() : need.getName();
            newFlow = queryService.connect( fromNode, toNode, name, priorId );
            if ( !newFlow.isExternal() )  // TODO: don't want to change the capability!
                initNewFlow( newFlow, queryService, need, capability );
            set( "satisfy", newFlow.getId() );
            set( "context", newFlow.getSegment().getId() );
            MultiCommand multi = (MultiCommand) get( "subCommands" );
            if ( multi == null ) {
                multi = new MultiCommand( getUserName(), "satisfy need - extra" );
                if ( needSegment.equals( capabilitySegment ) ) {
                    if ( !keepCapability )
                        multi.addCommand( new RemoveCapability( getUserName(), capability ) );
                    if ( !keepNeed )
                        multi.addCommand( new RemoveNeed( getUserName(), need ) );
                }
                set( "subCommands", multi );
            }
            multi.execute( commander );
            return new Change( Type.Added, newFlow );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    private static void initNewFlow( Flow newFlow, QueryService queryService, Flow need, Flow capability ) {
        newFlow.setEois( queryService.findCommonEOIs( capability, need ) );
        newFlow.setSignificanceToSource( capability.getSignificanceToSource() );
        newFlow.setSignificanceToTarget( need.getSignificanceToTarget() );
        newFlow.setChannels( Channel.intersect( capability.getChannels(),
                                                need.getChannels(),
                                                queryService.getPlanLocale() ) );
        newFlow.setMaxDelay( Delay.min( capability.getMaxDelay(), need.getMaxDelay() ) );
        newFlow.setIntent( capability.getIntent() != null ? capability.getIntent() : need.getIntent() );
        newFlow.setRestrictions( !capability.getRestrictions().isEmpty()
                ? capability.getRestrictions()
                : need.getRestrictions()
        );
        newFlow.setIfTaskFails( capability.isIfTaskFails() );
    }

    @Override
    public String getName() {
        return "satisfy info need";
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        try {
            MultiCommand multi = new MultiCommand( getUserName(), "unsatisfy need" );
            MultiCommand subCommands = (MultiCommand) get( "subCommands" );  // can be null
            subCommands.setMemorable( false );
            multi.addCommand( subCommands.getUndoCommand( commander ) );
            // Disconnect need satisfying flow
            Segment segment = commander.resolve( Segment.class, (Long) get( "context" ) );
            Flow newFlow = segment.findFlow( (Long) get( "satisfy" ) );
            multi.addCommand( commander.makeRemoveFlowCommand( getUserName(), newFlow ) );
            return multi;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }
}
