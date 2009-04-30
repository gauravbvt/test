package com.mindalliance.channels.attachments;

import com.mindalliance.channels.model.ModelObject;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.net.URL;
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
     * Attach a file to an object.
     * @param object the object
     * @param type the type of the attachment
     * @param fileUpload the thing
     */
    void attach( ModelObject object, Attachment.Type type, FileUpload fileUpload );

    /**
     * Attach an URL to an object.
     * @param object the object
     * @param type the type of the attachment
     * @param url the URL
     */
    void attach( ModelObject object, Attachment.Type type, URL url );

    /**
     * Detach something from an object. Should not complain if the attachment is not
     * actually attached.
     * @param object the object
     * @param attachment the attachment
     */
    void detach( ModelObject object, Attachment attachment );


    /**
     * Detach all attachment from an object.
     * @param object the object
     */
    void detachAll( ModelObject object );

}
