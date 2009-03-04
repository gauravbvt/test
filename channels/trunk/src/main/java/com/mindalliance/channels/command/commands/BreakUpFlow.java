package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;

/**
 * Command to break up a given flow.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 7:21:58 PM
 */
public class BreakUpFlow extends AbstractCommand {

    //TODO- restore full state of the flow on undo

    public BreakUpFlow( Flow flow ) {
        super();
        addConflicting( flow );
        addArgument( "state", flow.copy( ) );
        addArgument( "flow", flow.getId() );
        addArgument( "name", flow.getName() );
        needLockOn( flow );
        if ( flow.isInternal() ) {
            addArgument( "source", flow.getSource().getId() );
            addArgument( "target", flow.getTarget().getId() );
            if (flow.getSource().isPart()) {
                needLockOn(flow.getSource() );
                Scenario scenario = flow.getSource().getScenario();
                needLockOn(scenario);
                addArgument("scenario", scenario.getId());
            }
            if (flow.getTarget().isPart()) {
                needLockOn(flow.getTarget() );
                Scenario scenario = flow.getTarget().getScenario();
                needLockOn(scenario);
                addArgument("scenario", scenario.getId());
            }
        }
        else {
            ExternalFlow externalFlow = (ExternalFlow)flow;
            needLockOn(externalFlow.getPart());
            needLockOn(externalFlow.getConnector());
            Scenario scenario = externalFlow.getPart().getScenario();
            needLockOn(scenario);
            addArgument("scenario", scenario.getId());
            if (externalFlow.isPartTargeted()) {
                addArgument( "source", externalFlow.getConnector().getId() );
                addArgument( "target", externalFlow.getPart().getId()  );
            }
            else {
                addArgument( "target", externalFlow.getConnector().getId() );
                addArgument( "source", externalFlow.getPart().getId()  );
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
    public Object execute() throws NotFoundException {
        Scenario scenario = Project.service().find( Scenario.class, (Long)getArgument("scenario") );
        Flow flow = scenario.findFlow( (Long)getArgument("flow" ));
        flow.breakup();
        return null;
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
    public Command makeUndoCommand() throws CommandException {
        try {
            Scenario scenario = Project.service().find( Scenario.class, (Long) getArgument( "scenario" ) );
            Node source = scenario.getNode( (Long) getArgument( "source" ) );
            Node target = scenario.getNode( (Long) getArgument( "target" ) );
            String name = (String) getArgument( "name" );
            return new ConnectWithFlow( source, target, name );
        } catch ( NotFoundException e ) {
            throw new CommandException( "Can't undo" );
        }
    }
}
