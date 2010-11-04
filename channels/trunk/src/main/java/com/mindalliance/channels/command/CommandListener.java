package com.mindalliance.channels.command;

/**
 * A listener to command execution.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2010
 * Time: 2:14:18 PM
 */
public interface CommandListener {

    /**
     * A command was executed causing change.
     * @param command a Command
     * @param change a Change
     */
    void commandDone( Command command, Change change );

    /**
     * The execution of a command was undone.
     * @param command   a Command
     */
    void commandUndone( Command command );

    /**
     * The execution of a command was redone.
     * @param command   a Command
     */
    void commandRedone( Command command );

}
