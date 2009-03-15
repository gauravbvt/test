package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Change;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 9, 2009
 * Time: 1:07:55 PM
 */
public class TestAddRemoveRestoreScenario extends AbstractChannelsTest {

    private Commander commander;
    private Scenario scenario;
    private Service service;

    protected void setUp() {
        super.setUp();
        service = project.getService();
        commander = project.getCommander();
    }

    protected void tearDown() {
        if ( scenario != null ) service.remove( scenario );
    }

    public void testAddRemoveRestore() throws Exception {
        int count = countScenarios();
        Command addScenario = new AddScenario();
        assertTrue( commander.canDo( addScenario ) );
        Change change = commander.doCommand( addScenario );
        assertTrue( change.isAdded() );
        scenario = (Scenario) change.getSubject();
        assertTrue( countScenarios() == count + 1 );
        assertFalse( commander.canUndo() );
    }

    @SuppressWarnings( {"UnusedDeclaration"} )
    private int countScenarios() {
        int count = 0;
        List<Scenario> scenarios = service.list( Scenario.class );
        for ( Scenario sc : scenarios ) count++;
        return count;
    }


}
