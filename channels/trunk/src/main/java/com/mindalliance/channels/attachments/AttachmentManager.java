package com.mindalliance.channels.attachments;

import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.model.Attachment;
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
     * @param plan  a plan
     * @param url a url
     * @return a boolean
     */
    boolean exists( Plan plan, String url );

    /**
     * Upload a file and get an attachment.
     *
     * @param plan a plan
     * @param selectedType a type of attachment
     * @param name  a name for the attachment
     * @param upload       a file upload
     * @return an attachment or null if failed
     */
    Attachment upload( Plan plan, Attachment.Type selectedType, String name, FileUpload upload );

    /**
     * Get display label for an attachment.
     *
     * @param plan   a plan
     * @param attachment an attachment
     * @return a string
     */
    String getLabel( Plan plan, Attachment attachment );

    /**
     * Remove unattached documents.
     *
     * @param planDao   a plan dao
     */
    void removeUnattached( PlanDao planDao );

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

    /**
     * Whether url points to a video.
     * @param url a string
     * @return a boolean
     */
    boolean hasVideoContent( String url );

    /**
     * Whether url points to an image.
     * @param url a string
     * @return a boolean
     */
    boolean hasImageContent( String url );
}
