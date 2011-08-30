package com.mindalliance.channels.core;

import com.mindalliance.channels.core.Attachment.Type;

import java.util.List;

/**
 * An object that can have attachments.
 */
public interface Attachable {

    /**
     * Get attachments.
     *
     * @return a list a attachments
     */
    List<Attachment> getAttachments();

    /**
     * Set attachments.
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
     * Add an attachment. Do not call directly...
     *
     * @param attachment the attachment
     * @see AttachmentManager#addAttachment
     */
    void addAttachment( Attachment attachment );

    /**
     * Delete an attachment. Do not call directly...
     *
     * @param attachment the attachment
     * @see AttachmentManager#removeAttachment
     */
    void removeAttachment( Attachment attachment );
}
