package com.mindalliance.channels.command;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.NotFoundException;
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

    protected void setUp() {
        super.setUp();
        commander = project.getCommander();
        lockManager = project.getLockManager();
    }

    private AbstractCommand makeCommand() {
       AbstractCommand command = new AbstractCommand() {
            public String getName() {
                return "Simple command";
            }

            public Object execute() throws NotFoundException {
                System.out.println( "Hello! says " + getUserName());
                return true;
            }

            public boolean isUndoable() {
                return true;
            }

            public Command makeUndoCommand() {
                return new AbstractCommand() {
                    public String getName() {
                        return "Undo simple command";  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public Object execute() throws NotFoundException {
                        System.out.println( "Goodbye! says " + getUserName());
                        return true;
                    }

                    public boolean isUndoable() {
                        return false;
                    }

                    public Command makeUndoCommand() {
                        return null;
                    }
                };
            }
        };
        Scenario scenario = project.getService().getDefaultScenario();
        command.needLockOn( scenario );
        command.addConflicting( scenario );

        return command;
    }

    public void testExecuteSimpleCommand() {
        AbstractCommand command = makeCommand();
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
        } catch ( NotFoundException e ) {
            fail();
        }
    }

    public void testCommandLocking() throws Exception {
        AbstractCommand command = makeCommand();
        Scenario scenario = project.getService().getDefaultScenario();
        Lock lock = lockManager.grabLockOn( scenario.getId() );
        lock.setUserName( "bob" );
        command.needLockOn( scenario );
        assertFalse( commander.canDo( command ));
        try {
            commander.doCommand( command );
        }
        catch( CommandException e) {
            lockManager.releaseAllLocks( "bob" );
            assertTrue( commander.canDo( command ));
            commander.doCommand( command );
            lockManager.grabLockOn( scenario.getId() );
            try {
                commander.doCommand( command );
            }
            catch( CommandException exc) {
                fail();
            }
        }
    }

    public void testUndoingConflicts() throws Exception {
        AbstractCommand command = makeCommand();
        AbstractCommand otherUserCommand = makeCommand();
        otherUserCommand.setUserName( "bob" );
        commander.doCommand( command );
        Thread.sleep( 600 );
        commander.doCommand( otherUserCommand );
        assertFalse( commander.canUndo() );
        commander.doCommand( command );
        assertTrue( commander.canUndo() );
        commander.undo();
        assertFalse( commander.canUndo() );
        assertTrue( commander.canRedo() );
        commander.redo();
        assertFalse( commander.canRedo() );
        assertTrue( commander.canUndo() );
        commander.undo();
        commander.doCommand( otherUserCommand );
        assertFalse( commander.canRedo() );
        try {
            commander.redo();
            fail();
        }
        catch( CommandException e) {
            // ok
        }
    }
}
