package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Command;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 7:21:58 PM
 */
public class BreakUpFlow extends AbstractCommand {

    public BreakUpFlow( Flow flow ) {
        super();
        addArgument( "flowState", flow.copy() );
        addArgument( "flow", flow.getId() );
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
    public Command makeUndoCommand() {
        return new AbstractCommand() {
            public String getName() {
                return "Undo break up flow";
            }

            public Object execute() throws NotFoundException {
                Flow oldFlow = (Flow)getArgument("flowState");
                Scenario scenario = Project.service().find( Scenario.class, (Long)getArgument("scenario") );
                Node source = scenario.getNode( (Long)getArgument("source") );
                Node target = scenario.getNode( (Long)getArgument("target") );
                String name = (String)getArgument("name");
                Flow flow = Project.service().connect(source, target, name);
                flow.initFrom( oldFlow );
                return flow;
            }

            public Command makeUndoCommand() {
                throw new RuntimeException("Already an undo command." );
            }
        };
    }
}
