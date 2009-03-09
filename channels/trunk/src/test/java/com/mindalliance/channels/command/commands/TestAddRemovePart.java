package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.command.Commander;

import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 7, 2009
 * Time: 7:42:08 PM
 */
public class TestAddRemovePart extends AbstractChannelsTest {

    private Part part;
    private Commander commander;
    private Scenario scenario;
    private Service service;

    protected void setUp() {
        super.setUp();
        service = project.getService();
        commander = project.getCommander();
        scenario = service.createScenario();
        part = scenario.getDefaultPart();
        Part other = service.createPart( scenario );
        service.connect( part, other, "foo" );
        service.connect( service.createConnector( scenario ), part, "bar" );
    }

    protected void tearDown() {
        project.getService().remove( scenario );
    }

    public void testRemoveAddPart() throws Exception {
        assertTrue( countParts() == 2);
        assertTrue( countFlows() == 2);

        RemovePart removePart = new RemovePart( part );
        assertTrue( commander.canDo( removePart ) );
        assertTrue( (Boolean) commander.doCommand( removePart ) );
        assertTrue( countParts() == 1);
        assertTrue( countFlows() == 1);

        assertTrue( commander.canUndo() );
        commander.undo();
        assertTrue( countParts() == 2);
        assertTrue( countFlows() == 2);

        assertFalse( commander.canUndo() );
        assertTrue( commander.canRedo() );
        commander.redo();
        assertTrue( countParts() == 1);
        assertTrue( countFlows() == 1);

        assertTrue( commander.canUndo() );
        commander.undo();
        assertTrue( countParts() == 2);
        assertTrue( countFlows() == 2);

        assertTrue( commander.canRedo() );
        commander.redo();
        assertTrue( countParts() == 1);
        assertTrue( countFlows() == 1);
    }

    private int countParts() {
        int count = 0;
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            parts.next();
            count++;
        }
        return count;
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
