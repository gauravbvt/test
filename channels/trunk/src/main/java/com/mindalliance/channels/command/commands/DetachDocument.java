package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.AbstractAttachable;
import com.mindalliance.channels.model.Attachable;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.util.ChannelsUtils;

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

    public DetachDocument( ModelObject modelObject, String attachablePath, Attachment attachment ) {
        needLockOn( modelObject );
        set( "attachee", modelObject.getId() );
        set( "attachablePath", attachablePath );
        set( "url", attachment.getUrl() );
        set( "type", attachment.getType().name() );
        set( "name", attachment.getName() );
    }

    public String getName() {
        return "detach document";
    }

    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        Attachment attachment = getAttachment();
        String attachablePath = (String) get( "attachablePath" );
        Attachable attachable = (Attachable) ChannelsUtils.getProperty( mo, attachablePath, null );
        if ( attachable == null ) throw new CommandException( "Can't find where attachments are" );
        AttachmentManager attachmentManager = commander.getQueryService().getAttachmentManager();
        attachmentManager.removeAttachment( attachment, ( (AbstractAttachable) attachable ) );
        describeTarget( mo );                
        return new Change( Change.Type.Updated, mo, "attachments" );
    }


    public boolean isUndoable() {
        return true;
    }

    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        Attachment attachment = getAttachment();
        String attachablePath = (String) get( "attachablePath" );
        return new AttachDocument( mo, attachablePath, attachment );
    }

    private Attachment getAttachment() {
        return new Attachment(
                (String) get( "url" ),
                Attachment.Type.valueOf( (String) get( "type" ) ),
                (String) get( "name" )
        );
    }

}
