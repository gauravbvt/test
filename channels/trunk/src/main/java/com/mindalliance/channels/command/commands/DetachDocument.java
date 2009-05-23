package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.AttachmentManager;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.attachments.Attachment;
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
 * Detach if not already done and update model object tickets.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 15, 2009
 * Time: 3:15:16 PM
 */
public class DetachDocument extends AbstractCommand {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DetachDocument.class );

    public DetachDocument() {
    }

    public DetachDocument( ModelObject modelObject, Attachment attachment ) {
        assert modelObject != null;
        assert attachment != null;
        needLockOn( modelObject );
        set( "state", CommandUtils.getAttachmentState( modelObject, attachment ) );
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
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        Map<String, Object> state = (Map<String, Object>) get( "state" );
        ModelObject mo = commander.resolve( ModelObject.class, (Long) state.get( "object" ) );
        String ticket = findTicket(
                mo,
                (Map<String, Object>) get( "state" ),
                commander.getAttachmentManager() );
        if ( ticket != null ) {
            commander.getAttachmentManager().detach( ticket );
            mo.removeAttachmentTicket( ticket );
            set( "ticket", ticket );
            return new Change( Change.Type.Updated, mo, "attachmentTickets" );
        } else {
            LOG.warn( "Failed to detach document" );
            return new Change( Change.Type.None );
        }
    }

    private String findTicket(
            ModelObject mo,
            Map<String, Object> state,
            AttachmentManager attachmentManager ) {
        for ( String ticket : mo.getAttachmentTickets() ) {
            Attachment attachment = attachmentManager.getAttachment( ticket );
            if ( attachment.getType().name().equals( state.get( "type" ) )
                    && attachment.getUrl().equals( state.get( "url" ) )
                    && attachment.getDigest().equals( state.get( "digest" ) ) ) {
                return ticket;
            }
        }
        return null;
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
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        Map<String, Object> state = (Map<String, Object>) get( "state" );
        ModelObject mo = commander.resolve( ModelObject.class, (Long) state.get( "object" ) );
        String ticket = (String) get( "ticket" );
        AttachDocument command = new AttachDocument( mo, ticket );
        command.set( "state", state );
        return command;
    }
}
