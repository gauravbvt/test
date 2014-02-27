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
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Paste copied flow into edited part.
 */
public class PasteFlow extends AbstractCommand {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PasteFlow.class );


    public PasteFlow() {
        super( "daemon" );
    }

    public PasteFlow( String userName, Part part ) {
        super( userName );
        needLockOn( part );
        set( "segment", part.getSegment().getId() );
        set( "part", part.getId() );
    }

    @Override
    public String getName() {
        return "paste flow";
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
                && commander.isFlowCopied( getUserName() )
                && segment.isModifiabledBy( getUserName(), commander.getCommunityService() );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        CommunityService communityService = commander.getCommunityService();
        QueryService queryService = commander.getQueryService();
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Part part = (Part) segment.getNode( (Long) get( "part" ) );
            if ( part == null )
                throw new NotFoundException();
            Map<String, Object> copy;
            copy = (Map<String, Object>) get( "copy" );
            if ( copy == null ) {
                // not replaying or redoing
                copy = commander.getCopy( getUserName() );
                set( "copy", copy );
            }

            Long priorId = (Long) get( "flow" );
            boolean isSend = (Boolean) copy.get( "isSend" );
            Flow flow = isSend ?
                        queryService.connect( part,
                                              queryService.createConnector( segment ),
                                              (String) copy.get( "name" ),
                                              priorId ) :
                        queryService.connect( queryService.createConnector( segment ),
                                              part,
                                              (String) copy.get( "name" ),
                                              priorId );
            Map<String, Object> flowAttributes = (Map<String, Object>) copy.get( "attributes" );
            if ( flowAttributes != null ) {
                flow.initFromMap( flowAttributes, communityService );
            }
            set( "flow", flow.getId() );
            describeTarget( flow );
            return new Change( Type.Added, flow );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            Long flowId = (Long) get( "flow" );
            if ( flowId == null )
                throw new CommandException( "Can't undo." );
            else
                return commander.makeRemoveFlowCommand( getUserName(), segment.findFlow( flowId ) );
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo", e );
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public String getLabel( Commander commander ) throws CommandException {
        if ( commander.isFlowCopied( getUserName() ) ) {
            Map<String, Object> copy = commander.getCopy( getUserName() );
            boolean isSend = (Boolean) copy.get( "isSend" );
            return isSend ? "Paste capability" : "Paste need";
        } else
            return super.getLabel( commander );
    }
}
