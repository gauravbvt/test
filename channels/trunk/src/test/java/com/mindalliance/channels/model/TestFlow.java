package com.mindalliance.channels.model;

import junit.framework.TestCase;

public class TestFlow extends TestCase {

    private Flow flow;

    public TestFlow() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        flow = new Flow();
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
}
