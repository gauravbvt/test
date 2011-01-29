package com.mindalliance.channels.model;

import com.mindalliance.channels.attachments.AttachmentManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Attachable implementation.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 8, 2010
 * Time: 9:54:48 AM
 */
public class AbstractAttachable implements Attachable {

    /**
     * List of attachments.
     */
    private List<Attachment> attachments = new ArrayList<Attachment>();

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments( List<Attachment> attachments ) {
        this.attachments = attachments;
    }

    /**
     * {@inheritDoc}
     */
    public void addAttachment( Attachment attachment, AttachmentManager attachmentManager ) {
        if ( !attachments.contains( attachment ) ) {
            attachments.add( attachment );
        }
        attachmentAdded( attachment, attachmentManager );
    }

    /**
     * {@inheritDoc}
     */
    public List<Attachment.Type> getAttachmentTypes(  ) {
        List<Attachment.Type> types = new ArrayList<Attachment.Type>();
        types.add( Attachment.Type.Reference );
        types.add( Attachment.Type.Policy );
        return types;
    }

    @Override
    public void removeAttachment( Attachment attachment, AttachmentManager attachmentManager ) {
        getAttachments().remove( attachment );
        attachmentRemoved( attachment, attachmentManager );
    }

    /**
     * React to new attachment.
     * @param attachment an attachment
     */
    protected void attachmentAdded( Attachment attachment, AttachmentManager attachmentManager ) {
        // Do nothing
    }

    /**
     * React to removed attachment.
     * @param attachment an attachment
     */
     protected void attachmentRemoved( Attachment attachment, AttachmentManager attachmentManager ) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public List<Attachment.Type> getAttachmentTypes( String attachablePath ) {
        return getAttachmentTypes();
    }

    
}
