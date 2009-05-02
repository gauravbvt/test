package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;

import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 12, 2009
 * Time: 9:28:05 AM
 */
public class TestSatisfyNeed extends AbstractChannelsTest {

    private Scenario scenario;
    private QueryService queryService;
    Part source;
    Part target;
    Part otherTarget;
    Flow flow;
    Connector connector;

    protected void setUp() {
        super.setUp();
        queryService = app.getQueryService();
        scenario = queryService.createScenario();
        source = scenario.getDefaultPart();
        source.setRole( queryService.findOrCreate( Role.class, "source" ) );
        source.setTask( "doing source things" );
        target = queryService.createPart( scenario );
        target.setTask( "doing target things" );
        target.setRole( queryService.findOrCreate( Role.class, "target" ) );
        flow = queryService.connect( source, target, "info" );
        otherTarget = queryService.createPart( scenario );
        otherTarget.setTask( "doing other things" );
        otherTarget.setRole( queryService.findOrCreate( Role.class, "other target" ) );
        connector = queryService.createConnector( scenario );
        queryService.connect( connector, otherTarget, "info" );
    }

    protected void tearDown() {
        queryService.remove( scenario );
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
