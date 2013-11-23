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
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.core.query.QueryService;

import java.util.List;

/**
 * Disconnect a segment's external flows and then remove it.
 */
public class DisconnectAndRemoveSegment extends AbstractCommand {

    public DisconnectAndRemoveSegment() {
        super( "daemon" );
    }

    @SuppressWarnings( "unchecked" )
    public DisconnectAndRemoveSegment( String userName, Segment segment ) {
        super( userName );
        List<Part> internalParts = segment.listParts();
        List<Flow> internalFlows = segment.listFlows();
        List<Part> externalParts = segment.listExternalParts();
        needLocksOn( internalParts );
        needLocksOn( internalFlows );
        needLocksOn( externalParts );
        addConflicting( externalParts );
        set( "segment", segment.getId() );
    }

    @Override
    public String getName() {
        return "remove this segment";
    }

    @Override
    public boolean canDo( Commander commander ) {
        return super.canDo( commander ) && commander.getPlan().getSegmentCount() > 1;
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        describeTarget( segment );
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null )
            multi = makeSubCommands( segment, commander );
        set( "subCommands", multi );
        multi.execute( commander );
        return new Change( Type.Removed, segment );
    }

    private MultiCommand makeSubCommands( Segment segment, Commander commander ) {
        MultiCommand multi = new MultiCommand( getUserName(), "remove segment - extra" );
        for ( Flow externalFlow : segment.listExternalFlows() )
            multi.addCommand( commander.makeRemoveFlowCommand( getUserName(), externalFlow ) );
        CommunityService communityService = commander.getCommunityService();
        for ( UserIssue userIssue : communityService.findAllUserIssues( segment ) ) {
            RemoveIssue removeIssue = new RemoveIssue( getUserName(), userIssue );
            multi.addCommand( removeIssue );
        }
        multi.addCommand( new RemoveSegment( getUserName(), segment ) );
        return multi;
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( getUserName(), "restore segment" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

    @Override
    public boolean isSegmentSpecific() {
        return false;
    }
}
