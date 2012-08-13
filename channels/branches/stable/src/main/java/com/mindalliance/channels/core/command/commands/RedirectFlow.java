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
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;

/**
 * Redirect a flow to a new target or from a new source.
 */
public class RedirectFlow extends AbstractCommand {

    public RedirectFlow() {
        super( "daemon" );
    }

    public RedirectFlow( String userName, Flow flow, Node node, boolean nodeIsTarget ) {
        super( userName );
        needLockOn( flow );
        if ( node.isPart() )
            needLockOn( node );
        set( "segment", flow.getSegment().getId() );
        set( "flow", flow.getId() );
        set( "nodeSegment", node.getSegment().getId() );
        set( "node", node.getId() );
        set( "isTarget", nodeIsTarget );
    }

    @Override
    public String getName() {
        return "redirect flow";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        Flow flow = resolveFlow( (Long) get( "flow" ), segment );
        Segment nodeSegment = commander.resolve( Segment.class, (Long) get( "nodeSegment" ) );
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

    private MultiCommand makeSubCommands( Flow flow, Node other, boolean nodeIsTarget, Commander commander ) {
        MultiCommand multi = new MultiCommand( getUserName(), "reconnect - extra" );
        multi.setMemorable( false );
        ConnectWithFlow connect = nodeIsTarget ?
                                  new ConnectWithFlow( getUserName(),
                                                       flow.getSource(),
                                                       other,
                                                       flow.getName(),
                                                       flow.mapState() ) :
                                  new ConnectWithFlow( getUserName(),
                                                       other,
                                                       flow.getTarget(),
                                                       flow.getName(),
                                                       flow.mapState() );
        multi.addCommand( connect );
        // Remove prior flow. Don't carry over attributes to new flow.
        Command disconnect = commander.makeRemoveFlowCommand( getUserName(), flow );
        multi.addCommand( disconnect );
        multi.addLink( connect, "id", this, "newFlow" );
        return multi;
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( getUserName(), "unredirect" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }
}
