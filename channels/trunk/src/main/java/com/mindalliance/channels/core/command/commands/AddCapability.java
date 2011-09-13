package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;

import java.util.Map;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 6, 2009
 * Time: 9:05:33 AM
 */
public class AddCapability extends AbstractCommand {

    public AddCapability() {
    }

    public AddCapability( Part part ) {
        addConflicting( part );
        set( "part", part.getId() );
        set( "segment", part.getSegment().getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "add sharing capability";
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
            // Can be null
            Long priorFlowId = (Long) get( "flow" );
            Long priorConnectorId = (Long) get( "connector" );
            Flow flow = queryService.connect(
                    part,
                    queryService.createConnector( segment, priorConnectorId ),
                    (String) get( "name" ),
                    priorFlowId );
            describeTarget( flow );
            Map<String, Object> flowAttributes = (Map<String, Object>) get( "attributes" );
            if ( flowAttributes != null ) {
                ChannelsUtils.initialize( flow, flowAttributes );
            }
            set( "flow", flow.getId() );
            set( "connector", flow.getTarget().getId() );
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
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            return new RemoveCapability( flow );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
    }
}
