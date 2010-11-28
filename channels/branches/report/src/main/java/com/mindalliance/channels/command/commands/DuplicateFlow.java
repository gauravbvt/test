package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 4:43:29 PM
 */
public class DuplicateFlow extends AbstractCommand {

    public DuplicateFlow() {
    }

    public DuplicateFlow( Flow flow, boolean isSend ) {
        needLockOn( isSend ? flow.getSource() : flow.getTarget() );
        set( "segment", flow.getSegment().getId() );
        set( "flow", flow.getId() );
        set( "send", isSend );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "duplicate flow";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Flow duplicate;
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            if ( flow == null ) throw new NotFoundException();
            boolean isSend = (Boolean) get( "send" );
            duplicate = duplicate( commander.getQueryService(),
                                   flow, isSend, (Long) get( "duplicate" ) );
            describeTarget( duplicate );                    
            set( "duplicate", duplicate.getId() );
            return new Change( Change.Type.Added, duplicate );
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
            Long flowId = (Long) get( "duplicate" );
            if ( flowId == null ) {
                throw new CommandException( "Can't undo." );
            } else {
                Flow flow = segment.findFlow( flowId );
                return commander.makeRemoveFlowCommand( flow );
            }
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo", e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Flow flow;
        try {
            flow = segment.findFlow( (Long) get( "flow" ) );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh" );
        }
        if ( flow.isCapability() ) return "Duplicate capability";
        else if ( flow.isNeed() ) return "Duplicate need";
        else return "Duplicate flow";
    }

    /**
     * Make a duplicate of the flow
     *
     * @param queryService a query service
     * @param flow      a flow to duplicate
     * @param isSend whether to replicate as send or receive
     * @param priorId   Long or null
     * @return a created flow
     */
    public static Flow duplicate(
            QueryService queryService, Flow flow, boolean isSend, Long priorId ) {
        Flow duplicate;
        if ( isSend ) {
            Node source = flow.getSource();
            Segment segment = source.getSegment();
            duplicate = queryService.connect(
                    source,
                    queryService.createConnector( segment ),
                    flow.getName(),
                    priorId );
        } else {
            Node target = flow.getTarget();
            Segment segment = target.getSegment();
            duplicate = queryService.connect(
                    queryService.createConnector( segment ),
                    target,
                    flow.getName(),
                    priorId );
        }
        duplicate.initFrom( flow );
        return duplicate;
    }
}
