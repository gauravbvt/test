package com.mindalliance.channels.model;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.model.Attachment.Type;

import java.util.List;

/**
 * Can have attachments.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 8, 2010
 * Time: 9:50:23 AM
 */
public interface Attachable {
    /**
     * Get attachments.
     *
     * @return a list a attachments
     */
    List<Attachment> getAttachments();

    /**
     * Set attachments
     *
     * @param attachments a list a attachments
     */
    void setAttachments( List<Attachment> attachments );

    /**
     * Return list of meaningful types of attachments for class of model objects.
     *
     * @param attachablePath property path to attachable
     * @return a list of attachment types
     */
    List<Type> getAttachmentTypes( String attachablePath );


    /**
     * Return list of meaningful types of attachments for class of model objects.
     *
     * @return a list of attachment types
     */
    List<Type> getAttachmentTypes();

    /**
     * Add an attachment.
     * Do not call directly...
     * @see AttachmentManager#addAttachment
     * @param attachment the attachment
     */
    void addAttachment( Attachment attachment );

    /**
     * Delete an attachment.
     * Do not call directly...
     * @see AttachmentManager#removeAttachment
     * @param attachment the attachment
     */
    void removeAttachment( Attachment attachment );
}
