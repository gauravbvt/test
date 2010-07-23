package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.util.ChannelsUtils;

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
        setArguments( ChannelsUtils.getAttachmentState( attachment ) );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMemorable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "copy document";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        Map<String, Object> copy = new HashMap<String, Object>();
        copy.putAll( getArguments() );
        commander.setCopy( copy );
        Change change = new Change( Change.Type.None );
        change.setMessage( "Attachment copied" );
        return change;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        return null;
    }

}
