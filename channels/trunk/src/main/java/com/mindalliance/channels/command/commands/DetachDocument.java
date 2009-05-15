package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AttachmentManager;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.ModelObject;

/**
 * Detach if not already done and update model object tickets.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 15, 2009
 * Time: 3:15:16 PM
 */
public class DetachDocument extends AbstractCommand {

    public DetachDocument() {
    }

    public DetachDocument( ModelObject modelObject, String ticket ) {
        set( "modelObject", modelObject.getId() );
        set( "ticket", ticket );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "detach document";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "modelObject" ) );
        String ticket = (String) get( "ticket" );
        AttachmentManager attachmentManager = commander.getAttachmentManager();
        Attachment attachment = attachmentManager.getAttachment( ticket );
        // Don't detach if already detached
        if ( attachment != null ) {
            attachmentManager.detach( ticket );
        }
        mo.removeAttachmentTicket( ticket );
        return new Change( Change.Type.Updated, mo, "attachmentTickets" );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "modelObject" ) );
        String ticket = (String) get( "ticket" );
        return new AttachDocument( mo, ticket );
    }
}
