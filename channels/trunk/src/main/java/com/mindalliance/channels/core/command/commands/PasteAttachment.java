package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.Attachable;
import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.model.AttachmentImpl;
import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.Attachment.Type;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.util.ChannelsUtils;

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
        this( modelObject, "" );
    }

    public PasteAttachment( ModelObject modelObject, String attachablePath ) {
        assert modelObject != null;
        needLockOn( modelObject );
        set( "attachee", modelObject.getId() );
        set( "attachablePath", attachablePath );
    }

    @Override
    public String getName() {
        return "paste attachment";
    }

    @Override
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
        return commander.isReplaying() ? (Map<String, Object>) get( "copy" ) : commander.getCopy();
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        Map<String, Object> copy = getCopy( commander );
        if ( !commander.isReplaying() )
            set( "copy", copy );
        Attachment attachment = getAttachmentFromCopy( copy );
        String attachablePath = (String) get( "attachablePath" );
        Attachable attachable = (Attachable) ChannelsUtils.getProperty( mo, attachablePath, null );
        if ( attachable == null )
            throw new CommandException( "Can't find where attachments are" );
        AttachmentManager attachmentManager = commander.getQueryService().getAttachmentManager();
        attachmentManager.addAttachment( attachment, attachable );
        describeTarget( mo );
        return new Change( Change.Type.Updated, mo, "attachmentTickets" );
    }

    private static Attachment getAttachmentFromCopy( Map<String, Object> copy ) {
        return new AttachmentImpl(
                (String) copy.get( "url" ),
                Type.valueOf( (String) copy.get( "type" ) ),
                (String) copy.get( "name" ) );
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        Attachment attachment = getAttachmentFromCopy( (Map<String, Object>) get( "copy" ) );
        String attachablePath = (String) get( "attachablePath" );
        return new DetachDocument( mo, attachablePath, attachment );
    }

}
