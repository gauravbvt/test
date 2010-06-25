package com.mindalliance.channels.dao;

import com.mindalliance.channels.command.Command;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * List of commands to be persisted and replayed.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 22, 2009
 * Time: 10:27:24 AM
 */
public class Journal implements Serializable {

    private List<Command> commands;

    public Journal() {
        reset();
    }

    public void reset() {
        commands = Collections.synchronizedList( new ArrayList<Command>() );
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void addCommand( Command command ) {
        commands.add( command );
    }

    public int size() {
        return commands.size();
    }

    public boolean isEmpty() {
        return commands.isEmpty();
    }
}
