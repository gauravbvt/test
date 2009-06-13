package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;

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
        setArguments( CommandUtils.getAttachmentState( attachment ));
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
        return new Change( Change.Type.None, null );
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
