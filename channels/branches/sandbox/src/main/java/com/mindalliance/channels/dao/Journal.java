package com.mindalliance.channels.dao;

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

    private static final long serialVersionUID = -1994094393336425521L;

    private List<JournalCommand> commands =
            Collections.synchronizedList( new ArrayList<JournalCommand>() );

    public Journal() {
    }

    public void reset() {
        commands.clear();
    }

    public List<JournalCommand> getCommands() {
        return commands;
    }

    public void addCommand( JournalCommand command ) {
        commands.add( command );
    }

    public int size() {
        return commands.size();
    }

    public boolean isEmpty() {
        return commands.isEmpty();
    }
}
