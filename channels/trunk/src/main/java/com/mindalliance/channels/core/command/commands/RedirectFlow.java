package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;

/**
 * Redirect a flow to a new target or from a new source.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 11, 2009
 * Time: 1:11:22 PM
 */
public class RedirectFlow extends AbstractCommand {

    public RedirectFlow() {
    }

    public RedirectFlow( Flow flow, Node node, boolean nodeIsTarget ) {
        needLockOn( flow );
        if ( node.isPart() ) needLockOn( node );
        set( "segment", flow.getSegment().getId() );
        set( "flow", flow.getId() );
        set( "nodeSegment", node.getSegment().getId() );
        set( "node", node.getId() );
        set( "isTarget", nodeIsTarget );
    }

    public String getName() {
        return "redirect flow";
    }

    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Segment segment = commander.resolve(
                Segment.class,
                (Long) get( "segment" ) );
        Flow flow = resolveFlow( (Long) get( "flow" ), segment );
        Segment nodeSegment = commander.resolve(
                Segment.class,
                (Long) get( "nodeSegment" ) );
        Long nodeId = (Long) get( "node" );
        Node other = resolveNode( nodeId, nodeSegment, queryService );
        boolean nodeIsTarget = (Boolean) get( "isTarget" );
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( flow, other, nodeIsTarget, commander );
            set( "subCommands", multi );
        }
        // else command replay
        multi.execute( commander );
        Flow newFlow = resolveFlow( (Long) get( "newFlow" ), segment );
        describeTarget( newFlow );
        return new Change( Change.Type.Recomposed, newFlow );
    }

    private MultiCommand makeSubCommands(
            Flow flow,
            Node other,
            boolean nodeIsTarget,
            Commander commander ) {
        MultiCommand multi = new MultiCommand( "reconnect - extra" );
        multi.setMemorable( false );
        ConnectWithFlow connect = nodeIsTarget
                ? new ConnectWithFlow( flow.getSource(), other, flow.getName(), ChannelsUtils.getFlowAttributes( flow ) )
                : new ConnectWithFlow( other, flow.getTarget(), flow.getName(), ChannelsUtils.getFlowAttributes( flow ) );
        multi.addCommand( connect );
        // Remove prior flow. Don't carry over attributes to new flow.
        Command disconnect = commander.makeRemoveFlowCommand( flow );
        multi.addCommand( disconnect );
        multi.addLink( connect, "id", this, "newFlow" );
        return multi;
    }

    public boolean isUndoable() {
        return true;
    }

    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "unredirect" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

}
