package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.command.Change;

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
    private DataQueryObject dqo;

    protected void setUp() {
        super.setUp();
        dqo = app.getDqo();
        commander = app.getCommander();
        scenario = dqo.createScenario();
        part = scenario.getDefaultPart();
        Part other = dqo.createPart( scenario );
        dqo.connect( part, other, "foo" );
        dqo.connect( dqo.createConnector( scenario ), part, "bar" );
    }

    protected void tearDown() {
        dqo.remove( scenario );
    }

    public void testRemoveAddPart() throws Exception {
        assertTrue( countParts() == 2 );
        assertTrue( countFlows() == 2 );

        RemovePart removePart = new RemovePart( part );
        assertTrue( commander.canDo( removePart ) );
        Change change = commander.doCommand( removePart );
        assertTrue( change.isRecomposed() );
        assertTrue( change.getSubject() instanceof Scenario );
        assertTrue( countParts() == 1 );
        assertTrue( countFlows() == 1 );

        assertTrue( commander.canUndo() );
        assertTrue( commander.undo().isUnknown() );
        assertTrue( countParts() == 2 );
        assertTrue( countFlows() == 2 );

        assertFalse( commander.canUndo() );
        assertTrue( commander.canRedo() );
        assertTrue( commander.redo().isRecomposed() );
        assertTrue( countParts() == 1 );
        assertTrue( countFlows() == 1 );

        assertTrue( commander.canUndo() );
        commander.undo();
        assertTrue( countParts() == 2 );
        assertTrue( countFlows() == 2 );

        assertTrue( commander.canRedo() );
        commander.redo();
        assertTrue( countParts() == 1 );
        assertTrue( countFlows() == 1 );
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
