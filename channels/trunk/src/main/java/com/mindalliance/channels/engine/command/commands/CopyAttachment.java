package com.mindalliance.channels.engine.command.commands;

import com.mindalliance.channels.engine.command.AbstractCommand;
import com.mindalliance.channels.engine.command.Change;
import com.mindalliance.channels.engine.command.Command;
import com.mindalliance.channels.engine.command.CommandException;
import com.mindalliance.channels.engine.command.Commander;
import com.mindalliance.channels.core.model.Attachment;

import java.util.HashMap;
import java.util.Map;

/**
 * Take a copy of the attachment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 17, 2009
 * Time: 10:50:33 AM
 */
public class CopyAttachment extends AbstractCommand {

    public CopyAttachment() {
    }

    public CopyAttachment( Attachment attachment ) {
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
        commander.setCopy( copy );
        Change change = new Change( Change.Type.None );
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
