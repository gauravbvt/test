package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.attachments.FileAttachment;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.model.ModelObject;

import java.util.Map;

/**
 * Paste attachment into model object.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 17, 2009
 * Time: 11:17:15 AM
 */
public class PasteAttachment extends AbstractCommand {

    public PasteAttachment() {
    }

    public PasteAttachment( ModelObject modelObject ) {
        assert modelObject != null;
        needLockOn( modelObject );
        set( "attachee", modelObject.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "paste attachment";
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Commander commander ) {
        return super.canDo( commander)
                && commander.isAttachmentCopied()
                && attachmentExists( commander );
    }

    private boolean attachmentExists( Commander commander ) {
        Map<String, Object> copy = getCopy( commander );
        if ( isFileAttachmentCopied( copy ) ) {
            return commander.getAttachmentManager().findUploaded(
                    (String) copy.get( "url" ),
                    (String) copy.get( "digest" ) ) != null;
        } else {
            // Can always paste a url attachment
            return true;
        }
    }

    private boolean isFileAttachmentCopied( Map<String, Object> copy ) {
        return copy.get( "attachment" ).equals( FileAttachment.class.getSimpleName() );
    }

    @SuppressWarnings( "unchecked" )
    private Map<String, Object> getCopy( Commander commander ) {
        if ( commander.isReplaying() ) {
            return (Map<String, Object>) get( "copy" );
        } else {
            return commander.getCopy();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        Map<String, Object> copy = getCopy( commander );
        if ( !commander.isReplaying() ) {
            set( "copy", copy );
        }
        String ticket = CommandUtils.attach(
                copy,
                mo.getAttachmentTickets(),
                commander.getAttachmentManager() );
        if ( ticket != null ) {
            set( "ticket", ticket );
            mo.addAttachmentTicket( ticket );
            return new Change( Change.Type.Added, mo, "attachmentTickets" );
        } else {
            return new Change ( Change.Type.None );
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
    protected Command doMakeUndoCommand( Commander commander ) throws CommandException {
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        String ticket = (String) get( "ticket" );
        if ( mo == null || ticket == null ) {
            throw new CommandException( "Can't undo." );
        } else {
            return new DetachDocument( mo, commander.getAttachmentManager().getAttachment( ticket ) );
        }
    }

}
