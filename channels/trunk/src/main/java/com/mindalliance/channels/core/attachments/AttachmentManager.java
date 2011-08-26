package com.mindalliance.channels.core.attachments;

import com.mindalliance.channels.core.model.Attachable;
import com.mindalliance.channels.core.model.Attachment;
import com.mindalliance.channels.core.model.Plan;

import java.io.File;
import java.util.List;

// TODO Make interface depend on InputStream instead of File to allow non file-based implementation

/**
 * A thing that keeps track of the associations of model objects and their file attachments.
 */
public interface AttachmentManager {

    /**
     * Add an attachment to an attachable object.
     *
     * @param attachment the attachment
     * @param attachable the object
     */
    void addAttachment( Attachment attachment, Attachable attachable );

    /**
     * URL points to a document.
     *
     * @param plan a plan
     * @param url a url
     * @return a boolean
     */
    boolean exists( Plan plan, String url );

    /**
     * Get display label for an attachment.
     *
     * @param plan a plan
     * @param attachment an attachment
     * @return a string
     */
    String getLabel( Plan plan, Attachment attachment );

    /**
     * Get all media reference attachments.
     *
     * @param object the attachable object
     * @return a list of attachments
     */
    List<Attachment> getMediaReferences( Attachable object );

    /**
     * Get upload directory.
     *
     * @param plan the plan
     * @return a string
     */
    File getUploadDirectory( Plan plan );

    /**
     * Make full file path name from plan version-relative path.
     *
     * @param plan the context
     * @param planRelativePath a string
     * @return a file
     */
    File getUploadedFile( Plan plan, String planRelativePath );

    /**
     * Whether url points to an image.
     *
     * @param url a string
     * @return a boolean
     */
    boolean hasImageContent( String url );

    /**
     * Whether url points to a video.
     *
     * @param url a string
     * @return a boolean
     */
    boolean hasVideoContent( String url );

    /**
     * Whether the attachment is a reference image.
     *
     * @param attachment the attachment
     * @return a boolean
     */
    boolean isImageReference( Attachment attachment );

    /**
     * Whether the attachment is an image or video reference.
     *
     * @param attachment the attachment
     * @return a boolean
     */
    boolean isMediaReference( Attachment attachment );

    /**
     * Whether url points to uploaded file.
     *
     * @param url a string
     * @return a boolean
     */
    boolean isUploadedFileDocument( String url );

    /**
     * Whether the attachment is a reference movie.
     *
     * @param attachment the attachment
     * @return a boolean
     */
    boolean isVideoReference( Attachment attachment );

    /**
     * Remove an url attached to a plan.
     *
     * @param plan the plan
     * @param url the document url
     */
    void remove( Plan plan, String url );

    /**
     * Remove an attachment from an attachable object.
     *
     * @param attachment the attachment to remove
     * @param attachable the object
     */
    void removeAttachment( Attachment attachment, Attachable attachable );

    /**
     * Upload a file and get an attachment.
     *
     * @param plan a plan
     * @param upload what to upload
     * @return an attachment or null if failed
     */
    Attachment upload( Plan plan, Upload upload );
}
