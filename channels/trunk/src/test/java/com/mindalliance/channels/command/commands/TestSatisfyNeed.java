package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;

import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 12, 2009
 * Time: 9:28:05 AM
 */
public class TestSatisfyNeed extends AbstractChannelsTest {

    private Commander commander;
    private Scenario scenario;
    private DataQueryObject dqo;
    Part source;
    Part target;
    Part otherTarget;
    Flow flow;
    Connector connector;

    protected void setUp() {
        super.setUp();
        dqo = app.getDqo();
        commander = app.getCommander();
        scenario = dqo.createScenario();
        source = scenario.getDefaultPart();
        source.setRole( dqo.findOrCreate( Role.class, "source" ) );
        source.setTask( "doing source things" );
        target = dqo.createPart( scenario );
        target.setTask( "doing target things" );
        target.setRole( dqo.findOrCreate( Role.class, "target" ) );
        flow = dqo.connect( source, target, "info" );
        otherTarget = dqo.createPart( scenario );
        otherTarget.setTask( "doing other things" );
        otherTarget.setRole( dqo.findOrCreate( Role.class, "other target" ) );
        connector = dqo.createConnector( scenario );
        dqo.connect( connector, otherTarget, "info" );
    }

    protected void tearDown() {
        dqo.remove( scenario );
    }

    public void testSatisfyNeed() throws CommandException {
        assertTrue( countFlows( source.outcomes() ) == 1 );
        assertTrue( countFlows( target.requirements() ) == 1 );
        assertTrue( countFlows( otherTarget.requirements() ) == 1 );
        Command satisfyNeed = new SatisfyNeed( flow, connector.getInnerFlow(), flow.getScenario() );
        assertTrue( commander.canDo( satisfyNeed ) );
        assertTrue( commander.doCommand( satisfyNeed ).isAdded() );
        assertTrue( countFlows( source.outcomes() ) == 1 );
        assertTrue( countFlows( target.requirements() ) == 0 );
        assertTrue( countFlows( otherTarget.requirements() ) == 2 );
        assertTrue( commander.canUndo() );
        assertTrue( commander.undo().isUnknown() );
        assertTrue( countFlows( source.outcomes() ) == 1 );
        assertTrue( countFlows( target.requirements() ) == 1 );
        assertTrue( countFlows( otherTarget.requirements() ) == 1 );
        assertTrue( commander.canRedo() );
        assertTrue( commander.redo().isUnknown() );
        assertTrue( countFlows( source.outcomes() ) == 1 );
        assertTrue( countFlows( target.requirements() ) == 0 );
        assertTrue( countFlows( otherTarget.requirements() ) == 2 );
        assertTrue( commander.canUndo() );
    }

    private int countFlows( Iterator<Flow> flows ) {
        int count = 0;
        while ( flows.hasNext() ) {
            flows.next();
            count++;
        }
        return count;
    }
}
