package com.mindalliance.channels;

import com.mindalliance.channels.attachments.Attachment;
import org.apache.wicket.markup.html.form.upload.FileUpload;

/**
 * A thing that keeps track of the associations of model objects and their file attachments.
 */
public interface AttachmentManager {

    /**
     * URL points to a document.
     * @param url a url
     * @return a boolean
     */
    boolean exists( String url );

    /**
     * Upload a file and get an attachment.
     * @param selectedType a type of attachment
     * @param upload  a file upload
     * @return an attachment or null if failed
     */
    Attachment upload( Attachment.Type selectedType, FileUpload upload );

    /**
     * Get display label for an attachment.
     * @param attachment an attachment
     * @return a string
     */
    String getLabel( Attachment attachment );

    /**
     * Remove unattached documents.
     * @param service the service to use for finding objects
     */
    void removeUnattached( QueryService service );
}
