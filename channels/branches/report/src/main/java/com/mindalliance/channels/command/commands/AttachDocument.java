package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Attachable;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.util.ChannelsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add an attachment to a model object.
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

    public AttachDocument( ModelObject modelObject, String attachablePath, Attachment attachment ) {
        needLockOn( modelObject );
        set( "attachee", modelObject.getId() );
        set( "attachablePath", attachablePath );
        set( "url", attachment.getUrl() );
        set( "type", attachment.getType().name() );
        set( "name", attachment.getName() );
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
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        Attachment attachment = getAttachment();
        String attachablePath = (String) get( "attachablePath" );
        Attachable attachable = (Attachable) ChannelsUtils.getProperty( mo, attachablePath, null );
        if ( attachable == null ) throw new CommandException( "Can't find where attachments are" );
        attachable.addAttachment( attachment );
        describeTarget( mo );
        return new Change(
                Change.Type.Updated,
                mo,
                ( attachablePath.isEmpty() ? "attachments" : attachablePath + ".attachments" ) );
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
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        ModelObject mo = commander.resolve( ModelObject.class, (Long) get( "attachee" ) );
        String attachablePath = (String) get( "attachablePath" );
        Attachment attachment = getAttachment();
        return new DetachDocument( mo, attachablePath, attachment );
    }

    private Attachment getAttachment() {
        return new Attachment(
                (String) get( "url" ),
                Attachment.Type.valueOf( (String) get( "type" ) ),
                (String) get( "name" ) );
    }

}
