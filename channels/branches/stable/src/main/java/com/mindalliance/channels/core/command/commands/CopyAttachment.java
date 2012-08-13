/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Change.Type;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;

import java.util.HashMap;
import java.util.Map;

/**
 * Take a copy of the attachment.
 */
public class CopyAttachment extends AbstractCommand {

    public CopyAttachment() {
        super( "daemon" );
    }

    public CopyAttachment( String userName, Attachment attachment ) {
        super( userName );
        setArguments( getAttachmentState( attachment ) );
    }

    /**
     * Capture the state of an attachment.
     *
     * @param attachment an attachment
     * @return a map of attribute names and values
     */
    private static Map<String, Object> getAttachmentState( Attachment attachment ) {
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "type", attachment.getType().name() );
        state.put( "url", attachment.getUrl() );
        state.put( "name", attachment.getName() );
        return state;
    }

    @Override
    public boolean isMemorable() {
        return false;
    }

    @Override
    public String getName() {
        return "copy document";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        Map<String, Object> copy = new HashMap<String, Object>();
        copy.putAll( getArguments() );
        commander.setCopy( getUserName(), copy );
        Change change = new Change( Type.None );
        change.setMessage( "Attachment copied" );
        return change;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        return null;
    }

}
