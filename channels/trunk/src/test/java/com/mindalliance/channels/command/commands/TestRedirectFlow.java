package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Role;
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
public class TestRedirectFlow extends AbstractChannelsTest {

    private Commander commander;
    private Scenario scenario;
    private Service service;
    Part source;
    Part target;
    Part otherTarget;
    Flow flow;
    Connector connector;

    protected void setUp() {
        super.setUp();
        service = project.getService();
        commander = project.getCommander();
        scenario = service.createScenario();
        source = scenario.getDefaultPart();
        source.setRole( service.findOrCreate( Role.class, "source" ) );
        source.setTask( "doing source things" );
        target = service.createPart( scenario );
        target.setTask( "doing target things" );
        target.setRole( service.findOrCreate( Role.class, "target" ) );
        flow = service.connect( source, target, "info" );
        otherTarget = service.createPart( scenario );
        otherTarget.setTask( "doing other things" );
        otherTarget.setRole( service.findOrCreate( Role.class, "other target" ) );
        connector = service.createConnector( scenario );
        service.connect( connector, otherTarget, "info" );
    }

    protected void tearDown() {
        service.remove( scenario );
    }

    public void testRedirectFlow() throws CommandException {
        assertTrue( countFlows( source.outcomes() ) == 1 );
        assertTrue( countFlows( target.requirements() ) == 1 );
        assertTrue( countFlows( otherTarget.requirements() ) == 1 );
        Command redirectFlow = new RedirectFlow( flow, connector, true );
        assertTrue( commander.canDo( redirectFlow ) );
        assertTrue( commander.doCommand( redirectFlow ).isAdded() );
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
