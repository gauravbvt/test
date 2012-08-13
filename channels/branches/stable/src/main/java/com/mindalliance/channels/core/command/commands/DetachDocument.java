/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.Attachable;
import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Change.Type;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.AttachmentImpl;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.util.ChannelsUtils;

/**
 * Remove an attachment from a model object.
 */
public class DetachDocument extends AbstractCommand {

    public DetachDocument() {
        super( "daemon" );
    }

    public DetachDocument( String userName, ModelObject modelObject, String attachablePath, Attachment attachment ) {
        super( userName );
        needLockOn( modelObject );
        set( "attachee", modelObject.getId() );
        set( "attachablePath", attachablePath );
        set( "url", attachment.getUrl() );
        set( "type", attachment.getType().name() );
        set( "name", attachment.getName() );
    }

    @Override
    public String getName() {
        return "detach document";
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        Attachment attachment = getAttachment();
        String attachablePath = (String) get( "attachablePath" );
        Attachable attachable = (Attachable) ChannelsUtils.getProperty( mo, attachablePath, null );
        if ( attachable == null ) throw new CommandException( "Can't find where attachments are" );
        AttachmentManager attachmentManager = commander.getQueryService().getAttachmentManager();
        attachmentManager.removeAttachment( attachment, attachable );
        describeTarget( mo );                
        return new Change( Type.Updated, mo, "attachments" );
    }


    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        Attachment attachment = getAttachment();
        String attachablePath = (String) get( "attachablePath" );
        return new AttachDocument( getUserName(), mo, attachablePath, attachment );
    }

    private Attachment getAttachment() {
        return new AttachmentImpl(
                (String) get( "url" ),
                AttachmentImpl.Type.valueOf( (String) get( "type" ) ),
                (String) get( "name" )
        );
    }

}
