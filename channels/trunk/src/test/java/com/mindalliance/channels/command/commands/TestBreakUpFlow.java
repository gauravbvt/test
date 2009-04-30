package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Medium;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.Change;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 10:07:30 PM
 */
public class TestBreakUpFlow extends AbstractChannelsTest {

    private Scenario scenario;
    private Part source;
    private Part target;
    private BreakUpFlow command;
    private Commander commander;

    protected void setUp() {
        super.setUp();
        DataQueryObject dqo = app.getDqo();
        scenario = dqo.createScenario();
        source = scenario.getDefaultPart();
        source.setRole( dqo.findOrCreate( Role.class, "Manager" ) );
        target = scenario.createPart( dqo, dqo.findOrCreate( Role.class, "Employee" ), "nodding" );
        Flow flow = dqo.connect( source, target, "bizspeak" );
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
        commander = app.getCommander();
    }

    protected void tearDown() {
        app.getDqo().remove( scenario );
    }

    public void testInternalBreakUp() throws Exception {
        assertTrue( countFlows() == 1 );
        assertNotNull( findFlow() );
        assertTrue( commander.canDo( command ) );
        Change change = commander.doCommand( command );
        assertTrue( change.isRecomposed() );
        assertTrue( change.getSubject() instanceof Scenario );
        assertNull( findFlow() );
        assertTrue( countFlows() == 2 );
        assertTrue( commander.canUndo() );
        assertTrue( commander.undo().isUnknown() );
        Flow f = findFlow();
        assertNotNull( f );
        assertTrue( f.getName().equals( "bizspeak" ) );
        assertTrue( f.getDescription().equals( "Leveraging core values" ) );
        assertTrue( f.getMaxDelay().equals( new Delay( 5, Delay.Unit.minutes ) ) );
        assertTrue( f.getSignificanceToSource().equals( Flow.Significance.Terminates ) );
        assertTrue( f.getSignificanceToTarget().equals( Flow.Significance.Triggers ) );
        assertTrue( f.getChannels().size() == 2 );
        assertTrue( countFlows() == 1 );
        assertTrue( commander.canRedo() );
        assertTrue( commander.redo().isUnknown());
        assertNull( findFlow() );
        assertTrue( countFlows() == 2 );
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

    private int countFlows() {
        int count = 0;
        Iterator<Flow> flows = scenario.flows();
        while ( flows.hasNext() ) {
            flows.next();
            count++;
        }
        return count;
    }

}
