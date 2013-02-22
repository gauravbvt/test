/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;

import java.util.HashMap;
import java.util.Map;

/**
 * Command to connect source to target with flow of given name.
 */
public class ConnectWithFlow extends AbstractCommand {

    public ConnectWithFlow() {
        super( "daemon" );
    }

    public ConnectWithFlow( String userName ) {
        super( userName );
    }

    public ConnectWithFlow( String userName, Node source, Node target, String name ) {
        this( userName, source, target, name, new HashMap<String, Object>() );
    }

    public ConnectWithFlow( String userName, Node source, Node target, String name, Map<String, Object> attributes ) {
        this( userName );
        addConflicting( source );
        addConflicting( target );
        Part part;
        Node other;
        boolean isSend;
        if ( source.isPart() ) {
            isSend = true;
            part = (Part) source;
            other = target;
        } else {
            isSend = false;
            part = (Part) target;
            other = source;
        }
        Map<String, Object> args = new HashMap<String, Object>();
        args.put( "isSend", isSend );
        args.put( "segment", part.getSegment().getId() );
        args.put( "part", part.getId() );
        args.put( "otherSegment", other.getSegment().getId() );
        args.put( "other", other.getId() );
        args.put( "name", name );
        args.put( "attributes", attributes );
        setArguments( args );
    }

    @Override
    public String getName() {
        return "connect";
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        CommunityService communityService = commander.getCommunityService();
        QueryService queryService = commander.getQueryService();
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Part part = (Part) segment.getNode( (Long) get( "part" ) );
        Segment otherSegment = commander.resolve( Segment.class, (Long) get( "otherSegment" ) );
        Long nodeId = (Long) get( "other" );
        Node other = resolveNode( nodeId, otherSegment, queryService );
        Node actualOther;
        String name = (String) get( "name" );
        boolean isSend = (Boolean) get( "isSend" );
        Long priorId = (Long) get( "flow" );
        // Never connect to an internal connector.
        if ( other.isConnector() && Flow.isInternal( part, other ) ) {
            Connector internalConnector = (Connector) other;
            actualOther = isSend ?
                          internalConnector.getInnerFlow().getTarget() :
                          internalConnector.getInnerFlow().getSource();
            assert actualOther.isPart();
        } else
            actualOther = other;

        Flow flow = isSend ?
                    queryService.connect( part, actualOther, name, priorId ) :
                    queryService.connect( actualOther, part, name, priorId );
        assert priorId == null || priorId == flow.getId();
        set( "flow", flow.getId() );
        Map<String, Object> attributes = (Map<String, Object>) get( "attributes" );

        if ( attributes != null )
            flow.initFromMap( attributes, communityService );
        describeTarget( flow );
        return new Change( Change.Type.Added, flow );
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Long flowId = (Long) get( "flow" );
        if ( flowId == null )
            throw new CommandException( "Can't undo." );
        DisconnectFlow disconnectFlow = new DisconnectFlow( getUserName() );
        disconnectFlow.set( "segment", segment.getId() );
        disconnectFlow.set( "flow", flowId );
        return disconnectFlow;
    }
}
