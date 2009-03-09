package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.command.Commander;

import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 9, 2009
 * Time: 12:46:08 PM
 */
public class TestAddAndRemoveNeedAndCapability extends AbstractChannelsTest {

    private Commander commander;
    private Scenario scenario;
    private Service service;
    private Part part;

    protected void setUp() {
        super.setUp();
        service = project.getService();
        commander = project.getCommander();
        scenario = service.createScenario();
        part = scenario.getDefaultPart();
    }

    protected void tearDown() {
        service.remove( scenario );
    }

    public void testAddRemoveNeed() throws Exception {
        Flow need = (Flow)commander.doCommand( new AddNeed( part ));
        assertTrue( need.getTarget() == part);
        assertTrue( countNeeds( part ) == 1);
        assertTrue( commander.canUndo() );
        commander.undo();
        assertTrue( countNeeds( part ) == 0);
        assertTrue( commander.canRedo() );
        commander.redo();
        assertTrue( countNeeds( part ) == 1);
    }

    public void testAddRemoveCapability() throws Exception {
        Flow capability = (Flow)commander.doCommand( new AddCapability( part ));
        assertTrue( capability.getSource() == part);
        assertTrue( countCapabilities( part ) == 1);
        assertTrue( commander.canUndo() );
        commander.undo();
        assertTrue( countCapabilities( part ) == 0);
        assertTrue( commander.canRedo() );
        commander.redo();
        assertTrue( countCapabilities( part ) == 1);
    }

    private int countNeeds( Part part ) {
        int count = 0;
        Iterator<Flow> needs = part.requirements();
        while (needs.hasNext()) {
            needs.next();
            count ++;
        }
        return count;
    }

    private int countCapabilities( Part part ) {
        int count = 0;
        Iterator<Flow> outcomes = part.outcomes();
        while (outcomes.hasNext()) {
            outcomes.next();
            count ++;
        }
        return count;
    }


}
