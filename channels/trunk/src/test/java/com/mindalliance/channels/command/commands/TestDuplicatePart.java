package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Change;

import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 12, 2009
 * Time: 9:03:31 AM
 */
public class TestDuplicatePart extends AbstractChannelsTest {

    private Commander commander;
    private Scenario scenario;
    private DataQueryObject dqo;

    protected void setUp() {
        super.setUp();
        dqo = project.getDqo();
        commander = project.getCommander();
        scenario = dqo.createScenario();
    }

    protected void tearDown() {
        dqo.remove( scenario );
    }

    public void testDuplicatePart() throws Exception {
        int count = countParts();
        Part part = scenario.getDefaultPart();
        Command duplicatePart = new DuplicatePart( part );
        assertTrue( commander.canDo( duplicatePart ) );
        Change change = commander.doCommand( duplicatePart );
        assertTrue( change.isAdded() );
        Part duplicate = (Part) change.getSubject();
        assertTrue( duplicate.getName().equals( part.getName() ) );
        assertTrue( countParts() == count + 1 );
        assertTrue( commander.canUndo() );
        assertTrue( commander.undo().isRecomposed() );
        assertTrue( countParts() == count );
        assertTrue( commander.canRedo() );
        assertTrue( commander.redo().isUnknown() );
        assertTrue( countParts() == count + 1 );
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


}
