package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Attachment;
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
        return super.canDo( commander )
                && commander.isAttachmentCopied()
                && attachmentExists( commander );
    }

    private boolean attachmentExists( Commander commander ) {
        Map<String, Object> copy = getCopy( commander );
        return copy.get( "url" ) != null && copy.get( "type" ) != null;
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
        Attachment attachment = getAttachmentFromCopy( copy );
        mo.addAttachment( attachment );
        describeTarget( mo );                
        return new Change( Change.Type.Updated, mo, "attachmentTickets" );
    }

    private Attachment getAttachmentFromCopy( Map<String, Object> copy ) {
        return new Attachment(
                (String) copy.get( "url" ),
                Attachment.Type.valueOf( (String) copy.get( "type" ) ),
                (String) copy.get( "name" ) );
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
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        Attachment attachment = getAttachmentFromCopy( (Map<String, Object>) get( "copy" ) );
        return new DetachDocument( mo, attachment );
    }

}
