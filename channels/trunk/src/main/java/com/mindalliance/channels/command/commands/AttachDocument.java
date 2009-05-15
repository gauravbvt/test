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
 * Attach if not already done, or re-attach (undoing a detach), and update model object tickets.
 * This command is a noop unless called to re-attach a detached document.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 15, 2009
 * Time: 5:50:30 AM
 */
public class AttachDocument extends AbstractCommand {

    public AttachDocument() {
    }

    public AttachDocument( ModelObject modelObject, String ticket ) {
        set( "modelObject", modelObject.getId() );
        set( "ticket", ticket );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "attach document";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "modelObject" ) );
        String ticket = (String) get( "ticket" );
        AttachmentManager attachmentManager = commander.getAttachmentManager();
        Attachment attachment = attachmentManager.getAttachment( ticket );
        // Attempt to re-attach if not already attached
        if ( attachment == null ) {
            attachment = attachmentManager.reattach( ticket );
            if ( attachment == null ) throw new CommandException( " Can't reattach " + ticket + " to " + mo );
        }
        mo.addAttachmentTicket( ticket );
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
        return new DetachDocument( mo, ticket );
    }

}
