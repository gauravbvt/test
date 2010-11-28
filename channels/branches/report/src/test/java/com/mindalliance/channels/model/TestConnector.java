package com.mindalliance.channels.model;

import junit.framework.TestCase;
import com.mindalliance.channels.model.Connector;

/**
 * ...
 */
public class TestConnector extends TestCase {

    public TestConnector() {
    }

    public void testToString() {
        assertNotNull( new Connector().toString() );
    }
}
