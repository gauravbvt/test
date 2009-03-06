package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Delay;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;

import java.util.Map;
import java.util.HashMap;

/**
 * Command to break up a given flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 7:21:58 PM
 */
public class BreakUpFlow extends AbstractCommand {

    public BreakUpFlow( Flow flow ) {
        super();
        addConflicting( flow );
        addArgument( "state", getState( flow ) );
        addArgument( "flow", flow.getId() );
        addArgument( "scenario", flow.getScenario().getId() );
        addArgument( "name", flow.getName() );
        needLockOn( flow );
        if ( flow.isInternal() ) {
            addArgument( "source", flow.getSource().getId() );
            addArgument( "target", flow.getTarget().getId() );
            if ( flow.getSource().isPart() ) {
                needLockOn( flow.getSource() );
                Scenario scenario = flow.getSource().getScenario();
                needLockOn( flow.getSource().getScenario() );
                addArgument( "sourceScenario", scenario.getId() );
            }
            if ( flow.getTarget().isPart() ) {
                needLockOn( flow.getTarget() );
                Scenario scenario = flow.getTarget().getScenario();
                needLockOn( scenario );
                addArgument( "targetScenario", scenario.getId() );
            }
        } else {
            ExternalFlow externalFlow = (ExternalFlow) flow;
            needLockOn( externalFlow.getPart() );
            needLockOn( externalFlow.getConnector() );
            needLockOn( externalFlow.getPart().getScenario() );
            if ( externalFlow.isPartTargeted() ) {
                addArgument( "source", externalFlow.getConnector().getId() );
                addArgument( "target", externalFlow.getPart().getId() );
                addArgument( "sourceScenario", externalFlow.getConnector().getScenario().getId() );
                addArgument( "targetScenario", externalFlow.getPart().getScenario().getId() );
            } else {
                addArgument( "target", externalFlow.getConnector().getId() );
                addArgument( "source", externalFlow.getPart().getId() );
                addArgument( "targetScenario", externalFlow.getConnector().getScenario().getId() );
                addArgument( "sourceScenario", externalFlow.getPart().getScenario().getId() );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Break up flow";
    }

    /**
     * {@inheritDoc}
     */
    public Object execute( Service service ) throws CommandException {
        try {
            Scenario scenario = service.find( Scenario.class, (Long) getArgument( "scenario" ) );
            Flow flow = scenario.findFlow( (Long) getArgument( "flow" ) );
            flow.breakup();
            ignoreLock( (Long) getArgument( "flow" ) );
            return null;
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
    @SuppressWarnings( "unchecked" )
    public Command makeUndoCommand( Service service ) throws CommandException {
        try {
            Scenario scenario = service.find( Scenario.class, (Long) getArgument( "sourceScenario" ) );
            Node source = scenario.getNode( (Long) getArgument( "source" ) );
            scenario = service.find( Scenario.class, (Long) getArgument( "targetScenario" ) );
            Node target = scenario.getNode( (Long) getArgument( "target" ) );
            String name = (String) getArgument( "name" );
            Map<String, Object> state = (Map<String, Object>) getArgument( "state" );
            return new ConnectWithFlow( source, target, name, state );
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo", e );
        }
    }

    private Map<String, Object> getState( final Flow flow ) {
        return new HashMap<String, Object>() {
            {
                put( "name", flow.getName() );
                put( "description", flow.getDescription() );
                put( "askedFor", flow.isAskedFor() );
                put( "maxDelay", new Delay( flow.getMaxDelay() ) );
                put( "channels", flow.getChannelsCopy() );
                put( "significanceToTarget", flow.getSignificanceToTarget() );
                put( "significanceToSource", flow.getSignificanceToSource() );
            }
        };
    }
}
