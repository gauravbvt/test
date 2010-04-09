package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.ModelObject;

/**
 * Remove an attachment from a model object.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 15, 2009
 * Time: 3:15:16 PM
 */
public class DetachDocument extends AbstractCommand {

    public DetachDocument() {
    }

    public DetachDocument( ModelObject modelObject, Attachment attachment ) {
        needLockOn( modelObject );
        set( "attachee", modelObject.getId() );
        set( "url", attachment.getUrl() );
        set( "type", attachment.getType().name() );
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
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        Attachment attachment = getAttachment();
        mo.getAttachments().remove( attachment );
        return new Change( Change.Type.Updated, mo, "attachments" );
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
        Attachment attachment = getAttachment();
        return new AttachDocument( mo, attachment );
    }

    private Attachment getAttachment() {
        return new Attachment(
                (String)get("url"),
                Attachment.Type.valueOf( (String)get( "type") ));
    }

}
