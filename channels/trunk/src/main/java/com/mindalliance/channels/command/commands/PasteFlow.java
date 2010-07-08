package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.util.ChannelsUtils;

import java.util.Map;

/**
 * Paste copied flow into edited part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 20, 2009
 * Time: 4:06:16 PM
 */
public class PasteFlow extends AbstractCommand {

    public PasteFlow() {
    }

    public PasteFlow( Part part ) {
        needLockOn( part );
        set( "segment", part.getSegment().getId() );
        set( "part", part.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "paste";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Commander commander ) {
        return super.canDo( commander ) && commander.isFlowCopied();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Part part = (Part) segment.getNode( (Long) get( "part" ) );
            if ( part == null ) throw new NotFoundException();
            Map<String, Object> copy;
            copy = (Map<String, Object>) get( "copy" );
            if ( copy == null ) {
                // not replaying or redoing
                copy = commander.getCopy();
                set( "copy", copy );
            }

            Long priorId = (Long) get( "flow" );
            boolean isSend = (Boolean) copy.get( "isSend" );
            Flow flow;
            if ( isSend ) {
                flow = queryService.connect(
                        part,
                        queryService.createConnector( segment ),
                        (String) copy.get( "name" ),
                        priorId );
            } else {
                flow = queryService.connect(
                        queryService.createConnector( segment ),
                        part,
                        (String) copy.get( "name" ),
                        priorId );
            }
            Map<String, Object> flowAttributes = (Map<String, Object>) copy.get( "attributes" );
            if ( flowAttributes != null ) {
                ChannelsUtils.initialize( flow, flowAttributes );
            }
            set( "flow", flow.getId() );
            describeTarget( flow );                    
            return new Change( Change.Type.Added, flow );
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
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Long flowId = (Long) get( "flow" );
            if ( flowId == null ) {
                throw new CommandException( "Can't undo." );
            } else {
                Flow flow = segment.findFlow( flowId );
                return new DisconnectFlow( flow );
            }
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo", e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public String getLabel( Commander commander ) throws CommandException {
        Map<String, Object> copy = commander.getCopy();
        if ( copy == null ) {
            return super.getLabel( commander );
        } else {
            boolean isSend = (Boolean) copy.get( "isSend" );
            return isSend
                    ? "Paste capability"
                    : "Paste need";
        }
    }


}
