package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;

@SuppressWarnings( { "HardCodedStringLiteral" } )
public class TestFlow extends AbstractChannelsTest {

    private Flow flow;

    public TestFlow() {
    }

    @Override
    protected void setUp() {
        super.setUp();
        flow = new InternalFlow();
    }

    // Bogus tests for coverage
    public void testCritical() {
        assertFalse( flow.isCritical() );
        flow.setCritical( true );
        assertTrue( flow.isCritical() );
    }

    public void testAskedFor() {
        assertFalse( flow.isAskedFor() );
        flow.setAskedFor( true );
        assertTrue( flow.isAskedFor() );
    }

    public void testChannel() {
        assertTrue( flow.getChannels().isEmpty() );
        Channel channel = new Channel( Project.service().mediumNamed( "Phone" ), "800-123-4567" );
        flow.addChannel( channel );
        assertTrue( channel.isValid() );
        assertFalse(flow.getChannels().isEmpty() );
    }

    public void testMaxDelay() {
        assertEquals( 0, flow.getMaxDelay().getSeconds() );
        String s = "10 minutes";
        flow.setMaxDelay( s );
        assertEquals( s, flow.getMaxDelay().toString() );
    }

    public void testIsness() {
        assertTrue( new InternalFlow().isInternal() );
        assertFalse( new ExternalFlow().isInternal() );
    }

    public void testGetTitle() {
        // just to make sure no NPE...
        String s = "somebody notify somebody of something";
        assertEquals( s, flow.toString() );

        flow.setName( null );
        assertEquals( s, flow.toString() );
        flow.setName( " " );
        assertEquals( s, flow.toString() );

        flow.setAskedFor( true );
        assertEquals( "somebody ask somebody about something", flow.toString() );

        flow.setName( "something else" );
        assertEquals( "somebody ask somebody about something else", flow.toString() );
    }
}
