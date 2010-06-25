package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.TransmissionMedium;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 10:07:30 PM
 */
public class TestBreakUpFlow extends AbstractChannelsTest {

    private Segment segment;
    private Part source;
    private Part target;
    private BreakUpFlow command;

    private Commander commander;

    @Override
    public void setUp() throws IOException {
        super.setUp();

        commander = getCommander();

        segment = queryService.createSegment();
        source = segment.getDefaultPart();
        source.setRole( queryService.findOrCreate( Role.class, "Manager" ) );
        target = segment.createPart( queryService, queryService.findOrCreate( Role.class, "Employee" ), "nodding" );

        Flow flow = queryService.connect( source, target, "bizspeak" );
        flow.setDescription( "Leveraging core values" );
        flow.setMaxDelay( new Delay( 5, Delay.Unit.minutes ) );
        flow.setSignificanceToSource( Flow.Significance.Terminates );
        flow.setSignificanceToTarget( Flow.Significance.Triggers );
        flow.setChannels( new ArrayList<Channel>() {
            {
                add( new Channel(queryService.findOrCreateType(
                TransmissionMedium.class,
                "Phone" ), "800-555-1212" ) );
                add( new Channel( queryService.findOrCreateType(
                TransmissionMedium.class,
                "Email" ), "stuff@acme.com" ) );
            }
        } );
        ElementOfInformation eoi = new ElementOfInformation();
        eoi.setContent( "content" );
        eoi.setSources( "sources" );
        eoi.setSpecialHandling( "handling" );
        flow.addEoi( eoi );
        command = new BreakUpFlow( flow );
    }

    @Override
    @After
    public void tearDown() {
        queryService.remove( segment );
        super.tearDown();
    }

    @Test
    public void testInternalBreakUp() throws Exception {
        assertSame( 1, countFlows() );
        assertNotNull( findFlow() );
        assertTrue( commander.canDo( command ) );

        Change change = commander.doCommand( command );
        assertTrue( change.isRecomposed() );
        assertTrue( change.getSubject( getCommander().getQueryService() ) instanceof Segment );
        assertNull( findFlow() );
        assertSame( 2, countFlows() );
        assertTrue( commander.canUndo() );
        assertTrue( commander.undo().isUnknown() );

        Flow f = findFlow();
        assertNotNull( f );
        assertEquals( "bizspeak", f.getName() );
        assertEquals( "Leveraging core values", f.getDescription() );
        assertEquals( new Delay( 5, Delay.Unit.minutes ), f.getMaxDelay() );
        assertEquals( Flow.Significance.Terminates, f.getSignificanceToSource() );
        assertEquals( Flow.Significance.Triggers, f.getSignificanceToTarget() );
        assertSame( 2, f.getChannels().size() );
        List<ElementOfInformation> eois = f.getEois();
        assertSame( 1, eois.size() );
        ElementOfInformation eoi = eois.get( 0 );
        assertEquals( eoi.getContent(), "content" );
        assertEquals( eoi.getSources(), "sources" );
        assertEquals( eoi.getSpecialHandling(), "handling" );
        assertSame( 1, countFlows() );
        assertTrue( commander.canRedo() );
        assertTrue( commander.redo().isUnknown() );
        assertNull( findFlow() );
        assertSame( 2, countFlows() );
    }

    private Flow findFlow() {
        Flow result = null;
        Iterator<Flow> sends = source.sends();
        while ( result == null && sends.hasNext() ) {
            Flow f = sends.next();
            if ( f.getTarget() == target ) result = f;
        }
        return result;
    }

    private int countFlows() {
        int count = 0;
        Iterator<Flow> flows = segment.flows();
        while ( flows.hasNext() ) {
            flows.next();
            count++;
        }
        return count;
    }

}
