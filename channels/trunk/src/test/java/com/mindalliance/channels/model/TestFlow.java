package com.mindalliance.channels.model;

import com.mindalliance.channels.AbstractChannelsTest;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;

@SuppressWarnings( { "HardCodedStringLiteral" } )
public class TestFlow extends AbstractChannelsTest {

    private Flow flow;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        flow = new InternalFlow();
    }

    // Bogus tests for coverage
    @Test
    public void testCritical() {
        assertFalse( flow.isCritical() );
        flow.becomeCritical( );
        assertTrue( flow.isCritical() );
    }

    @Test
    public void testAskedFor() {
        assertFalse( flow.isAskedFor() );
        flow.setAskedFor( true );
        assertTrue( flow.isAskedFor() );
    }

    @Test
    public void testChannel() {
        assertTrue( flow.getChannels().isEmpty() );
        Channel channel = new Channel( queryService.findOrCreateType(
                TransmissionMedium.class,
                "Phone" ), "800-123-4567" );
        flow.addChannel( channel );
        assertTrue( channel.isValid() );
        assertFalse( flow.getChannels().isEmpty() );
    }

    @Test
    public void testMaxDelay() {
        assertEquals( 0, flow.getMaxDelay().getSeconds() );
        String s = "10 minutes";
        flow.setMaxDelay( s );
        assertEquals( s, flow.getMaxDelay().toString() );
    }

    @Test
    public void testIsness() {
        assertTrue( new InternalFlow().isInternal() );
        assertFalse( new ExternalFlow().isInternal() );
    }

    @Test
    public void testGetTitle() {
        // just to make sure no NPE...
        String s = "somebody notify somebody of \"something\"";
        assertEquals( s, flow.toString() );

        flow.setName( null );
        assertEquals( s, flow.toString() );
        flow.setName( " " );
        assertEquals( s, flow.toString() );

        flow.setAskedFor( true );
        assertEquals( "somebody ask somebody about \"something\"", flow.toString() );

        flow.setName( "something else" );
        assertEquals( "somebody ask somebody about \"something else\"", flow.toString() );
    }
}
