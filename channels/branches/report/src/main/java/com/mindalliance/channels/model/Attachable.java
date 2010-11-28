package com.mindalliance.channels.model;

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
     * Add an attachment.
     *
     * @param attachment an attachment
     */
    void addAttachment( Attachment attachment );

    /**
     * Return list of meaningful types of attachments for class of model objects.
     *
     * @param attachablePath property path to attachable
     * @return a list of attachment types
     */
    List<Attachment.Type> getAttachmentTypes( String attachablePath );


    /**
     * Return list of meaningful types of attachments for class of model objects.
     *
     * @return a list of attachment types
     */
    List<Attachment.Type> getAttachmentTypes(  );

}
