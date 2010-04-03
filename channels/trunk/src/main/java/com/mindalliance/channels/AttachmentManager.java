package com.mindalliance.channels;

import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.model.Plan;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.io.File;

/**
 * A thing that keeps track of the associations of model objects and their file attachments.
 */
public interface AttachmentManager {

    /**
     * URL points to a document.
     *
     * @param plan
     * @param url a url
     * @return a boolean
     */
    boolean exists( Plan plan, String url );

    /**
     * Upload a file and get an attachment.
     *
     * @param plan
     * @param selectedType a type of attachment
     * @param upload       a file upload
     * @return an attachment or null if failed
     */
    Attachment upload( Plan plan, Attachment.Type selectedType, FileUpload upload );

    /**
     * Get display label for an attachment.
     *
     * @param plan
     * @param attachment an attachment
     * @return a string
     */
    String getLabel( Plan plan, Attachment attachment );

    /**
     * Remove unattached documents.
     *
     * @param service the service to use for finding objects
     * @param plan
     */
    void removeUnattached( QueryService service, Plan plan );

    /**
     * Get upload directory.
     *
     * @param plan the plan
     * @return a string
     */
    File getUploadDirectory( Plan plan );

    /**
      * Get upload path.
      *
      * @return a string
      */
    String getUploadPath();
}
