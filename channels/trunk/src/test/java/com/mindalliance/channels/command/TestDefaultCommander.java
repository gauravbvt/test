package com.mindalliance.channels.command;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.command.commands.HelloCommand;
import com.mindalliance.channels.model.Segment;

import java.io.IOException;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 11:00:14 AM
 */
public class TestDefaultCommander extends AbstractChannelsTest {

    protected void setUp() throws IOException {
        super.setUp();
        commander.reset();
    }

    public void testExecuteSimpleCommand() {
        AbstractCommand command = HelloCommand.makeCommand( "hello", commander );
        try {
            assertTrue( commander.canDo( command ) );
            assertFalse( commander.canUndo() );
            Change change;
            change = commander.doCommand( command );
            assertTrue( change.isUnknown() );
            assertTrue( commander.canUndo() );
            assertTrue( commander.undo().isUnknown() );
            assertTrue( commander.canRedo() );
            assertTrue( commander.redo().isUnknown() );
            assertFalse( commander.canRedo() );
        } catch ( CommandException e ) {
            fail();
        }
    }

    public void testCommandLocking() throws Exception {
        AbstractCommand command = HelloCommand.makeCommand( "hello", commander );
        Segment segment = app.getQueryService().getDefaultSegment();
        Lock lock = lockManager.grabLockOn( segment.getId() );
        lock.setUserName( "bob" );
        command.needLockOn( segment );
        assertFalse( commander.canDo( command ) );
        try {
            commander.doCommand( command );
        }
        catch ( CommandException e ) {
            lockManager.releaseAllLocks( "bob" );
            assertTrue( commander.canDo( command ) );
            commander.doCommand( command );
            lockManager.grabLockOn( segment.getId() );
            try {
                commander.doCommand( command );
            }
            catch ( CommandException exc ) {
                fail();
            }
        }
    }

    public void testUndoingConflicts() throws Exception {
        AbstractCommand command = HelloCommand.makeCommand( "hello", commander );
        AbstractCommand otherUserCommand = HelloCommand.makeCommand( "hello", commander );
        otherUserCommand.setUserName( "bob" );
        commander.doCommand( command );
        Thread.sleep( 10 );
        commander.doCommand( otherUserCommand );
        assertFalse( commander.canUndo() );
        Thread.sleep( 10 );
        commander.doCommand( command );
        assertTrue( commander.canUndo() );
        commander.undo();
        assertFalse( commander.canUndo() );
        assertTrue( commander.canRedo() );
        commander.redo();
        assertFalse( commander.canRedo() );
        assertTrue( commander.canUndo() );
        Thread.sleep( 10 );
        commander.undo();
        Thread.sleep( 10 );
        commander.doCommand( otherUserCommand );
        assertFalse( commander.canRedo() );
        try {
            commander.redo();
            fail();
        }
        catch ( CommandException e ) {
            // ok
        }
    }
}
