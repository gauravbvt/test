package com.mindalliance.channels;

import junit.framework.TestCase;

@SuppressWarnings( { "HardCodedStringLiteral" } )
public class TestFlow extends TestCase {

    private Flow flow;

    public TestFlow() {
    }

    @Override
    protected void setUp() throws Exception {
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
        assertNull( flow.getChannel() );
        final String s = "Bla";
        flow.setChannel( s );
        assertSame( s, flow.getChannel() );
    }

    public void testMaxDelay() {
        assertNull( flow.getMaxDelay() );
        final String s = "Bla";
        flow.setMaxDelay( s );
        assertSame( s, flow.getMaxDelay() );
    }

    public void testIsness() {
        assertTrue( new InternalFlow().isInternal() );
        assertFalse( new ExternalFlow().isInternal() );
    }

    public void testGetTitle() {
        // just to make sure no NPE...
        final String s = "somebody notifying somebody of something";
        assertEquals( s, flow.toString() );

        flow.setName( null );
        assertEquals( s, flow.toString() );
        flow.setName( " " );
        assertEquals( s, flow.toString() );

        flow.setAskedFor( true );
        assertEquals( "somebody asking somebody about something", flow.toString() );

        flow.setName( "something else" );
        assertEquals( "somebody asking somebody about something else", flow.toString() );
    }
}
