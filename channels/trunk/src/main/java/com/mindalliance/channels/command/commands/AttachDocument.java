package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.ModelObject;
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

    public AttachDocument( ModelObject modelObject, Attachment attachment ) {
        needLockOn( modelObject );
        set( "attachee", modelObject.getId() );
        set( "url", attachment.getUrl() );
        set( "type", attachment.getType().name() );
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
        mo.addAttachment( attachment );
        return new Change( Change.Type.Added, mo, "attachments" );
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
        Attachment attachment = getAttachment();
        return new DetachDocument( mo, attachment );
    }

    private Attachment getAttachment() {
        return new Attachment(
                (String)get("url"),
                Attachment.Type.valueOf( (String)get( "type") ));
    }

}
