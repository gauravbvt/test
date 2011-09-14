/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command;

/**
 * A listener to command execution.
 */
public interface CommandListener {

    /**
     * A command was executed causing change.
     *
     * @param command a Command
     * @param change a Change
     * @param uri a sanitized plan uri
     */
    void commandDone( Command command, Change change, String uri );

    /**
     * The execution of a command was undone.
     *
     * @param command a Command
     * @param uri a sanitized plan uri
     */
    void commandUndone( Command command, String uri );

    /**
     * The execution of a command was redone.
     *
     * @param command a Command
     * @param uri a sanitized plan uri
     */
    void commandRedone( Command command, String uri );
}
