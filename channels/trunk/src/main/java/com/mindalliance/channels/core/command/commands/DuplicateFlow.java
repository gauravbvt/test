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
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuplicateFlow extends AbstractCommand {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DuplicateFlow.class );


    public DuplicateFlow() {
        super( "daemon" );
    }

    public DuplicateFlow( String userName, Flow flow, boolean isSend ) {
        super( userName );
        needLockOn( isSend ? flow.getSource() : flow.getTarget() );
        needLockOn( flow );
        set( "segment", flow.getSegment().getId() );
        set( "flow", flow.getId() );
        set( "send", isSend );
    }

    @Override
    public String getName() {
        return "duplicate flow";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            if ( flow == null )
                throw new NotFoundException();
            boolean isSend = (Boolean) get( "send" );
            Flow duplicate = duplicate( commander.getQueryService(), flow, isSend, (Long) get( "duplicate" ) );
            describeTarget( duplicate );
            set( "duplicate", duplicate.getId() );
            return new Change( Type.Added, duplicate );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    @Override
    public boolean canDo( Commander commander ) {
        Segment segment = null;
        try {
            segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        } catch( CommandException e ) {
            LOG.warn( "Segment not found", e );
            return false;
        }
        return super.canDo( commander )
                && segment.isModifiabledBy( getUserName(), commander.getCommunityService() );
    }


    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Long flowId = (Long) get( "duplicate" );
            if ( flowId == null ) {
                throw new CommandException( "Can't undo." );
            } else {
                Flow flow = segment.findFlow( flowId );
                return commander.makeRemoveFlowCommand( getUserName(), flow );
            }
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo", e );
        }
    }

    @Override
    public String getLabel( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        try {
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            return flow.isCapability() ? "Duplicate capability"
                                       : flow.isNeed() ? "Duplicate need" : "Duplicate flow";
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh" );
        }
    }

    /**
     * Make a duplicate of the flow.
     *
     * @param queryService a query service
     * @param flow a flow to duplicate
     * @param isSend whether to replicate as send or receive
     * @param priorId Long or null
     * @return a created flow
     */
    public static Flow duplicate( QueryService queryService, Flow flow, boolean isSend, Long priorId ) {
        Flow duplicate;
        if ( isSend ) {
            Node source = flow.getSource();
            Segment segment = source.getSegment();
            duplicate =
                    queryService.connect( source, queryService.createConnector( segment ), flow.getName(), priorId );
        } else {
            Node target = flow.getTarget();
            Segment segment = target.getSegment();
            duplicate =
                    queryService.connect( queryService.createConnector( segment ), target, flow.getName(), priorId );
        }
        duplicate.initFrom( flow );
        return duplicate;
    }
}
