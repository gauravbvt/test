package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.util.ChannelsUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Command to connect source to target with flow of given name.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 1:44:34 PM
 */
public class ConnectWithFlow extends AbstractCommand {

    public ConnectWithFlow() {
    }

    public ConnectWithFlow( final Node source, final Node target, final String name ) {
        this( source, target, name, new HashMap<String, Object>() );
    }

    public ConnectWithFlow(
            final Node source,
            final Node target,
            final String name,
            final Map<String, Object> state ) {
        super();
        needLockOn( source );
        needLockOn( target );
        final Part part;
        final Node other;
        final boolean isSend;
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
        args.put( "attributes", state );
        setArguments( args );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "connect";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Segment segment = commander.resolve(
                Segment.class,
                (Long) get( "segment" ) );
        Part part = (Part) segment.getNode( (Long) get( "part" ) );
        Segment otherSegment = commander.resolve(
                Segment.class,
                (Long) get( "otherSegment" ) );
        Long nodeId = (Long) get( "other" );
        Node other = ChannelsUtils.resolveNode( nodeId, otherSegment, queryService );
        String name = (String) get( "name" );
        boolean isSend = (Boolean) get( "isSend" );
        Long priorId = (Long) get( "flow" );
        Flow flow = isSend
                ? queryService.connect( part, other, name, priorId )
                : queryService.connect( other, part, name, priorId );
        assert priorId == null || priorId == flow.getId();
        set( "flow", flow.getId() );
        Map<String, Object> attributes = (Map<String, Object>) get( "attributes" );
        if ( attributes != null ) {
            ChannelsUtils.initialize( flow, attributes );
        }
        return new Change( Change.Type.Added, flow );
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
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Long flowId = (Long) get( "flow" );
        if ( flowId == null ) throw new CommandException( "Can't undo." );
        DisconnectFlow disconnectFlow = new DisconnectFlow();
        disconnectFlow.set( "segment", segment.getId() );
        disconnectFlow.set( "flow", flowId );
        return disconnectFlow;
    }
}
