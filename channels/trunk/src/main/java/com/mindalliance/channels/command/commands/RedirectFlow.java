package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Scenario;

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
        needLockOn( node );
        set( "scenario", flow.getScenario().getId() );
        set( "flow", flow.getId() );
        set( "nodeScenario", node.getScenario().getId() );
        set( "node", node.getId() );
        set( "isTarget", nodeIsTarget );
    }

    public String getName() {
        return "redirect";
    }

    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Scenario scenario = commander.resolve(
                Scenario.class,
                (Long) get( "scenario" ) );
        Flow flow = CommandUtils.resolveFlow( (Long) get( "flow" ), scenario );
        Scenario nodeScenario = commander.resolve(
                Scenario.class,
                (Long) get( "nodeScenario" ) );
        Long nodeId = (Long) get( "node" );
        Node other = CommandUtils.resolveNode( nodeId, nodeScenario, queryService );
        boolean nodeIsTarget = (Boolean) get( "isTarget" );
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( flow, other, nodeIsTarget );
            set( "subCommands", multi );
        }
        // else command replay
        multi.execute( commander );
        Flow newFlow = CommandUtils.resolveFlow( (Long) get( "newFlow" ), scenario );
        return new Change( Change.Type.Recomposed, newFlow );
    }

    private MultiCommand makeSubCommands(
            Flow flow,
            Node other,
            boolean nodeIsTarget ) {
        MultiCommand multi = new MultiCommand( "reconnect - extra" );
        multi.setMemorable( false );
        ConnectWithFlow connect = nodeIsTarget
                ? new ConnectWithFlow( flow.getSource(), other, flow.getName() )
                : new ConnectWithFlow( other, flow.getTarget(), flow.getName() );
        multi.addCommand( connect );
        // Remove prior flow. Don't carry over attributes to new flow.
        DisconnectFlow disconnect = new DisconnectFlow( flow );
        multi.addCommand( disconnect );
        multi.addLink( connect, "id", this, "newFlow" );
        return multi;
    }

    public boolean isUndoable() {
        return true;
    }

    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "unredirect" );
        multi.setUndoes( getName() );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

}
