package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Delay;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary and Confidential. User: jf Date: Mar 4,
 * 2009 Time: 10:07:30 PM
 */
public class TestBreakUpFlow extends AbstractChannelsTest {

    private Segment segment;

    private Part source;

    private Part target;

    private BreakUpFlow command;

    private Commander commander;

    /**
     * Convenience accessor for tests.
     *
     * @param role the role for the new part
     * @param task the task of the new part
     * @return the new part
     */
    private Part createPart( Role role, String task ) {
        Part result = queryService.createPart( segment );
        result.setRole( role );
        result.setTask( task );
        return segment.addNode( result );
    }

    @Override
    public void setUp() throws IOException {
        super.setUp();

        commander = getCommander();

        segment = queryService.createSegment();
        source = segment.getDefaultPart();
        source.setRole( queryService.findOrCreate( Role.class, "Manager" ) );
        target = createPart( queryService.findOrCreate( Role.class, "Employee" ), "nodding" );

        Flow flow = queryService.connect( source, target, "bizspeak" );
        flow.setDescription( "Leveraging core values" );
        flow.setMaxDelay( new Delay( 5, Delay.Unit.minutes ) );
        flow.setSignificanceToSource( Flow.Significance.Terminates );
        flow.setSignificanceToTarget( Flow.Significance.Triggers );
        flow.setChannels( new ArrayList<Channel>() {
            {
                add( new Channel( queryService.findOrCreateType( TransmissionMedium.class, "Phone" ),
                                  "800-555-1212" ) );
                add( new Channel( queryService.findOrCreateType( TransmissionMedium.class, "Email" ),
                                  "stuff@acme.com" ) );
            }
        } );
        ElementOfInformation eoi = new ElementOfInformation();
        eoi.setContent( "content" );
        eoi.setDescription( "description" );
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
    public void testInternalBreakUp() {
        assertSame( 1, countFlows() );
        Flow f0 = findFlow();
        assertNotNull( f0 );
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
        assertEquals( "content", eoi.getContent() );
        assertEquals( "description", eoi.getDescription() );
        assertEquals( "handling", eoi.getSpecialHandling() );
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
            if ( f.getTarget() == target )
                result = f;
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
