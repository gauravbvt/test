package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;

import java.util.Iterator;
import java.io.IOException;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 9, 2009
 * Time: 12:46:08 PM
 */
public class TestAddAndRemoveNeedAndCapability extends AbstractChannelsTest {

    private Scenario scenario;
    private QueryService queryService;
    private Part part;

    protected void setUp() throws IOException {
        super.setUp();
        queryService = app.getQueryService();
        scenario = queryService.createScenario();
        part = scenario.getDefaultPart();
    }

    protected void tearDown() {
        queryService.remove( scenario );
    }

    public void testAddRemoveNeed() throws Exception {
        Change change = commander.doCommand( new AddNeed( part ) );
        Flow need = (Flow) change.getSubject();
        assertTrue( change.isAdded() );
        assertTrue( need.getTarget() == part );
        assertTrue( countNeeds( part ) == 1 );
        assertTrue( commander.canUndo() );
        assertTrue( commander.undo().isRemoved() );
        assertTrue( countNeeds( part ) == 0 );
        assertTrue( commander.canRedo() );
        assertTrue( commander.redo().isAdded() );
        assertTrue( countNeeds( part ) == 1 );
    }

    public void testAddRemoveCapability() throws Exception {
        Change change = commander.doCommand( new AddCapability( part ) );
        assertTrue( change.isAdded() );
        Flow capability = (Flow) change.getSubject();
        assertTrue( capability.getSource() == part );
        assertTrue( countCapabilities( part ) == 1 );
        assertTrue( commander.canUndo() );
        assertTrue( commander.undo().isRemoved() );
        assertTrue( countCapabilities( part ) == 0 );
        assertTrue( commander.canRedo() );
        assertTrue( commander.redo().isAdded() );
        assertTrue( countCapabilities( part ) == 1 );
    }

    private int countNeeds( Part part ) {
        int count = 0;
        Iterator<Flow> needs = part.requirements();
        while ( needs.hasNext() ) {
            needs.next();
            count++;
        }
        return count;
    }

    private int countCapabilities( Part part ) {
        int count = 0;
        Iterator<Flow> outcomes = part.outcomes();
        while ( outcomes.hasNext() ) {
            outcomes.next();
            count++;
        }
        return count;
    }


}
