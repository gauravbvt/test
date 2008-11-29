package com.mindalliance.channels;

import junit.framework.TestCase;

public class TestScenarioNode extends TestCase {

    private ScenarioNode node;

    public TestScenarioNode() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        node = new ScenarioNode();
    }

    public void testScenario() {
        assertNull( node.getEmbeddedScenario() );
        final Scenario scenario = new Scenario();
        node.setEmbeddedScenario( scenario );
        assertSame( scenario, node.getEmbeddedScenario() );
    }

    public void testTitle() {
        node.setEmbeddedScenario( new Scenario( "Bla" ) );
        assertEquals( node.getEmbeddedScenario().toString(), node.getTitle() );
    }
}
