package com.mindalliance.channels.model;

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
        assertNull( node.getScenario() );
        final Scenario scenario = new Scenario();
        node.setScenario( scenario );
        assertSame( scenario, node.getScenario() );       
    }
}
