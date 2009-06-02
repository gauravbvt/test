package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AttachmentManager;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.attachments.Document;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.model.ModelObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AttachDocument.class );

    public AttachDocument() {
    }

    public AttachDocument( ModelObject modelObject, String ticket ) {
        needLockOn( modelObject );
        set( "attachee", modelObject.getId() );
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
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        ModelObject mo = getAttachee( commander );
        // commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        String ticket = (String) get( "ticket" );
        AttachmentManager attachmentManager = commander.getAttachmentManager();
        Document document = attachmentManager.getDocument( ticket );
        if ( document == null ) {
            document = attachmentManager.reattach( ticket );
            if ( document == null ) {
                // Attachment needs to be re-created
                Map<String, Object> attachmentState = (Map<String, Object>) get( "state" );
                assert attachmentState != null;
                ticket = CommandUtils.attach(
                        attachmentState,
                        mo.getAttachmentTickets(),
                        commander.getAttachmentManager() );
            }
        }
        // avoid duplication
        mo.removeAttachmentTicket( (String) get( "ticket" ) );
        set( "ticket", ticket );
        if ( ticket != null ) {
            mo.addAttachmentTicket( ticket );
            return new Change( Change.Type.Updated, mo, "attachmentTickets" );
        } else {
            LOG.warn( "Failed to attach document to " + mo );
            return new Change( Change.Type.None );
        }
    }

    // Work around because of bug in Jettison JSON XML driver
    @SuppressWarnings( "unchecked" )
    private ModelObject getAttachee( Commander commander ) throws CommandException {
        if ( get( "attachee" ) == null ) {
            Map<String, Object> state = (Map<String, Object>) get( "state" );
            return commander.resolve( ModelObject.class, (Long) state.get( "object" ) );
        } else {
            return commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return get( "ticket" ) != null;
    }

    /**
     * {@inheritDoc}
     */
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        String ticket = (String) get( "ticket" );
        return new DetachDocument( mo, commander.getAttachmentManager().getDocument( ticket ) );
    }

}
