package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Delay;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Medium;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 10:07:30 PM
 */
public class TestBreakUpConnectFlow extends AbstractChannelsTest {

    private Scenario scenario;
    private Part source;
    private Part target;
    private BreakUpFlow command;
    private Commander commander;

    protected void setUp() {
        super.setUp();
        Service service = project.getService();
        scenario = service.createScenario();
        source = scenario.getDefaultPart();
        source.setRole( service.findOrCreate( Role.class, "Manager" ) );
        target = scenario.createPart( service, service.findOrCreate( Role.class, "Employee" ), "nodding" );
        Flow flow = service.connect( source, target, "bizspeak" );
        flow.setDescription( "Leveraging core values" );
        flow.setMaxDelay( new Delay( 5, Delay.Unit.minutes ) );
        flow.setSignificanceToSource( Flow.Significance.Terminates );
        flow.setSignificanceToTarget( Flow.Significance.Triggers );
        flow.setChannels( new ArrayList<Channel>() {
            {
                add( new Channel( Medium.Phone, "800-555-1212" ) );
                add( new Channel( Medium.Email, "stuff@acme.com" ) );
            }
        } );
        command = new BreakUpFlow( flow );
        commander = project.getCommander();
    }

    protected void tearDown() {
        project.getService().remove( scenario );
    }

    public void testInternalBreakUp() throws CommandException {
        assertNotNull( findFlow() );
        assertTrue( commander.canDo( command ) );
        commander.doCommand( command );
        assertNull( findFlow() );
        assertTrue( commander.canUndo() );
        commander.undo();
        Flow f = findFlow();
        assertNotNull( f );
        assertTrue( f.getName().equals( "bizspeak" ) );
        assertTrue( f.getDescription().equals( "Leveraging core values" ) );
        assertTrue( f.getMaxDelay().equals( new Delay( 5, Delay.Unit.minutes ) ) );
        assertTrue( f.getSignificanceToSource().equals( Flow.Significance.Terminates ) );
        assertTrue( f.getSignificanceToTarget().equals( Flow.Significance.Triggers ) );
        assertTrue( f.getChannels().size() == 2 );
/*        assertTrue( commander.canRedo() );
        commander.redo();
        assertNull( findFlow() );*/
    }

    private Flow findFlow() {
        Flow result = null;
        Iterator<Flow> outcomes = source.outcomes();
        while ( result == null && outcomes.hasNext() ) {
            Flow f = outcomes.next();
            if ( f.getTarget() == target ) result = f;
        }
        return result;
    }
}
