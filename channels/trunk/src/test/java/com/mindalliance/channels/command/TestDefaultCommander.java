package com.mindalliance.channels.command;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Scenario;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 11:00:14 AM
 */
public class TestDefaultCommander extends AbstractChannelsTest {

    private Commander commander;
    private LockManager lockManager;

    public class HelloCommand extends AbstractCommand {


        public HelloCommand( String greeting ) {
            addArgument( "greeting", greeting );
        }

        public String getName() {
            return "Hello";
        }

        public Object execute( Commander commander ) throws CommandException {
            System.out.println( get( "greeting" ) + "! says " + getUserName() );
            return true;
        }

        public boolean isUndoable() {
            return true;
        }

        protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
            return makeCommand( "not " + get( "greeting" ) );
        }
    }

    protected void setUp() {
        super.setUp();
        commander = project.getCommander();
        lockManager = project.getLockManager();
        commander.reset();
    }

    private AbstractCommand makeCommand( String greeting ) {
        AbstractCommand command = new HelloCommand( greeting );
        Scenario scenario = project.getService().getDefaultScenario();
        command.addConflicting( scenario );
        return command;
    }

    public void testExecuteSimpleCommand() {
        AbstractCommand command = makeCommand( "hello" );
        try {
            assertTrue( commander.canDo( command ) );
            assertFalse( commander.canUndo() );
            boolean result;
            result = (Boolean) commander.doCommand( command );
            assertTrue( result );
            assertTrue( commander.canUndo() );
            commander.undo();
            assertTrue( commander.canRedo() );
            commander.redo();
            assertFalse( commander.canRedo() );
        } catch ( CommandException e ) {
            fail();
        }
    }

    public void testCommandLocking() throws Exception {
        AbstractCommand command = makeCommand( "hello" );
        Scenario scenario = project.getService().getDefaultScenario();
        Lock lock = lockManager.grabLockOn( scenario.getId() );
        lock.setUserName( "bob" );
        command.needLockOn( scenario );
        assertFalse( commander.canDo( command ) );
        try {
            commander.doCommand( command );
        }
        catch ( CommandException e ) {
            lockManager.releaseAllLocks( "bob" );
            assertTrue( commander.canDo( command ) );
            commander.doCommand( command );
            lockManager.grabLockOn( scenario.getId() );
            try {
                commander.doCommand( command );
            }
            catch ( CommandException exc ) {
                fail();
            }
        }
    }

    public void testUndoingConflicts() throws Exception {
        AbstractCommand command = makeCommand( "hello" );
        AbstractCommand otherUserCommand = makeCommand( "hello" );
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
