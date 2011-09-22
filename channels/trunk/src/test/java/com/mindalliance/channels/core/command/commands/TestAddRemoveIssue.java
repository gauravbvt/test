package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.engine.analysis.Analyst;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

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
    public void testAddRemoveIssue() {
        Commander commander = getCommander();

        int count = countIssues( segment );
        Command addIssue = new AddUserIssue( User.current().getUsername(), segment );
        assertTrue( commander.canDo( addIssue ) );

        Change change = commander.doCommand( addIssue );
        assertTrue( change.isAdded() );
        UserIssue issue = (UserIssue) change.getSubject( commander.getQueryService() );
        assertSame( count + 1, countIssues( segment ) );

        issue.setDescription( "fubar" );
        assertTrue( commander.canUndo( User.current().getUsername() ) );
        assertTrue( commander.undo( User.current().getUsername() ).isRemoved() );
        assertSame( count, countIssues( segment ) );
        assertTrue( commander.canRedo( User.current().getUsername() ) );
        assertTrue( commander.redo( User.current().getUsername() ).isAdded() );
        assertSame( count + 1, countIssues( segment ) );

        boolean found = false;
        for ( Issue i : analyst.listIssues( queryService, segment, false ) ) {
            found = found || i.getDescription().equals( "fubar" );
        }
        assertTrue( found );
    }

    private int countIssues( ModelObject mo ) {
        return analyst.listIssues( queryService, mo, false ).size();
    }
}
