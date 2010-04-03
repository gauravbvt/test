package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.UserIssue;
import org.junit.After;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.io.IOException;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 9, 2009
 * Time: 8:27:16 PM
 */
public class TestAddRemoveIssue extends AbstractChannelsTest {

    private Segment segment;
    private Analyst analyst;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        analyst = wicketApplication.getAnalyst();
        segment = queryService.createSegment();
    }

    @Override
    @After
    public void tearDown() {
        queryService.remove( segment );
        super.tearDown();
    }

    @Test
    public void testAddRemoveIssue() throws Exception {
        Commander commander = getCommander();

        int count = countIssues( segment );
        Command addIssue = new AddUserIssue( segment );
        assertTrue( commander.canDo( addIssue ) );

        Change change = commander.doCommand( addIssue );
        assertTrue( change.isAdded() );
        UserIssue issue = (UserIssue) change.getSubject();
        assertSame( count + 1, countIssues( segment ) );

        issue.setDescription( "fubar" );
        assertTrue( commander.canUndo() );
        assertTrue( commander.undo().isRemoved() );
        assertSame( count, countIssues( segment ) );
        assertTrue( commander.canRedo() );
        assertTrue( commander.redo().isAdded() );
        assertSame( count + 1, countIssues( segment ) );

        boolean found = false;
        for ( Issue i : analyst.listIssues( segment, false ) ) {
            found = found || i.getDescription().equals( "fubar" );
        }
        assertTrue( found );
    }

    private int countIssues( ModelObject mo ) {
        return analyst.listIssues( mo, false ).size();
    }
}
