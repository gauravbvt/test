package com.mindalliance.channels.command;

import java.util.List;
import java.util.Date;

/**
 * A persistent list of mementoes.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 28, 2009
 * Time: 2:24:34 PM
 */
public interface History {
    /**
     * Keep a memento of a command.
     * @param command a command
     * @return a memento
     */
    Memento remember( Command command );

    /**
     * Get undo command available to the user.
     *
     * @return a command or null
     */
    Command getUndo();

    /**
     * Get redo command available to the user.
     *
     * @return a command or null
     */
    Command getRedo();    

    /**
     * Get all mementoes for the current user since a given date.
     * @param date a date
     * @return a list of mementoes
     */
    List<Memento> getMementoesSince( Date date );

    /**
     * Get all mementoes for a given user since a given date.
     * @param userName a user's name
     * @param date a date
     * @return a list of mementoes
     */
    List<Memento> getMementoesSince( String userName, Date date );

}
