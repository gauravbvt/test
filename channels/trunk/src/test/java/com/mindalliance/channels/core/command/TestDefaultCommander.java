package com.mindalliance.channels.core.command;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.core.command.commands.HelloCommand;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Segment;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 11:00:14 AM
 */
public class TestDefaultCommander extends AbstractChannelsTest {

    private Commander commander;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        commander = getCommander();
    }

    @Test
    public void testExecuteSimpleCommand() {
        AbstractCommand command = HelloCommand.makeCommand( "hello", commander );
        assertTrue( commander.canDo( command ) );
        assertFalse( commander.canUndo( User.current().getUsername() ) );

        Change change = commander.doCommand( command );
        assertTrue( change.isUnknown() );
        assertTrue( commander.canUndo( User.current().getUsername() ) );
        assertTrue( commander.undo( User.current().getUsername() ).isUnknown() );
        assertTrue( commander.canRedo( User.current().getUsername() ) );
        assertTrue( commander.redo( User.current().getUsername() ).isUnknown() );
        assertFalse( commander.canRedo( User.current().getUsername() ) );
        if ( change.isFailed() ) {
            fail();
        }
    }

    @Test
    public void testCommandLocking() throws Exception {
        LockManager lockManager = getLockManager();
        AbstractCommand command = HelloCommand.makeCommand( "hello", commander );
        Segment segment = commander.getQueryService().getDefaultSegment();
        lockManager.lock( "bob", segment.getId() );
        command.needLockOn( segment );
        assertFalse( commander.canDo( command ) );
        Change change = commander.doCommand( command );
        if ( change.isFailed() ) {
            lockManager.release( "bob" );
            assertTrue( commander.canDo( command ) );
            commander.doCommand( command );
            lockManager.lock( "guest", segment.getId() );
            if ( commander.doCommand( command ).isFailed() ) {
                fail();
            }
        }
    }

    @Test
    public void testUndoingConflicts() throws Exception {
        AbstractCommand command = HelloCommand.makeCommand( "hello", commander );
        AbstractCommand otherUserCommand = HelloCommand.makeCommand( "hello", commander );
        otherUserCommand.setUserName( "bob" );
        commander.doCommand( command );
        Thread.sleep( 10 );
        commander.doCommand( otherUserCommand );
        assertFalse( commander.canUndo( User.current().getUsername() ) );
        Thread.sleep( 10 );
        commander.doCommand( command );
        assertTrue( commander.canUndo( User.current().getUsername() ) );
        commander.undo( User.current().getUsername() );
        assertFalse( commander.canUndo( User.current().getUsername() ) );
        assertTrue( commander.canRedo( User.current().getUsername() ) );
        commander.redo( User.current().getUsername() );
        assertFalse( commander.canRedo( User.current().getUsername() ) );
        assertTrue( commander.canUndo( User.current().getUsername() ) );
        Thread.sleep( 10 );
        commander.undo( User.current().getUsername() );
        Thread.sleep( 10 );
        commander.doCommand( otherUserCommand );
        assertFalse( commander.canRedo( User.current().getUsername() ) );
        if ( !commander.redo( User.current().getUsername() ).isFailed() )
            fail();
    }
}
