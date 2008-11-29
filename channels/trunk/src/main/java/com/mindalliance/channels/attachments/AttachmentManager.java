package com.mindalliance.channels.attachments;

import com.mindalliance.channels.ModelObject;

import java.util.Iterator;

/**
 * A thing that keeps track of the associations of model objects and their file attachments.
 */
public interface AttachmentManager {

    /**
     * Iterates over attachments of an object.
     * @param object the object
     * @return an iterator over attachments
     */
    Iterator<Attachment> attachments( ModelObject object );

    /**
     * Attach something to an object.
     * @param object the object
     * @param attachment the thing
     */
    void attach( ModelObject object, Attachment attachment );

    /**
     * Detach something from an object. Should ot complain if the attachment is not
     * actually attached.
     * @param object the object
     * @param attachment the attachment
     */
    void detach( ModelObject object, Attachment attachment );

}
