package com.mindalliance.channels.attachments;

import com.mindalliance.channels.model.Attachable;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Attachment.Type;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.util.Loader;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.io.File;
import java.util.List;

/** A thing that keeps track of the associations of model objects and their file attachments. */
public interface AttachmentManager {

    void reloadTags( Plan plan );

    /**
     * URL points to a document.
     * @param plan a plan
     * @param url a url
     *
     * @return a boolean
     */
    boolean exists( Plan plan, String url );

    /**
     * Upload a file and get an attachment.
     * @param plan a plan
     * @param selectedType a type of attachment
     * @param name a name for the attachment
     * @param upload a file upload
     *
     * @return an attachment or null if failed
     */
    Attachment upload( Plan plan, Type selectedType, String name, FileUpload upload );

    /**
     * Get display label for an attachment.
     * @param plan a plan
     * @param attachment an attachment
     *
     * @return a string
     */
    String getLabel( Plan plan, Attachment attachment );

    /**
     * Get upload directory.
     * @param plan the plan
     *
     * @return a string
     */
    File getUploadDirectory( Plan plan );

    /**
     * Get upload path.
     * @return a string
     */
    String getUploadPath();

    /**
     * Whether url points to a video.
     * @param url a string
     *
     * @return a boolean
     */
    boolean hasVideoContent( String url );

    /**
     * Whether url points to an image.
     * @param url a string
     *
     * @return a boolean
     */
    boolean hasImageContent( String url );

    /**
     * Get all media reference attachments.
     * @param object
     *
     * @return a list of attachments
     */
    List<Attachment> getMediaReferences( ModelObject object );

    /**
     * Whether the attachment is an image or video reference.
     * @param attachment the attachment
     *
     * @return a boolean
     */
    boolean isMediaReference( Attachment attachment );

    /**
     * Whether the attachment is a reference movie.
     * @param attachment the attachment
     *
     * @return a boolean
     */
    boolean isVideoReference( Attachment attachment );

    /**
     * Whether the attachment is a reference image.
     * @param attachment the attachment
     *
     * @return a boolean
     */
    boolean isImageReference( Attachment attachment );

    /**
     * Whether url points to uploaded file.
     * @param url a string
     *
     * @return a boolean
     */
    boolean isUploadedFileDocument( String url );

    /**
     * Make full file path name from plan version-relative path.
     *
     * @param plan the context
     * @param planRelativePath a string
     *
     * @return a file
     */
    File getUploadedFile( Plan plan, String planRelativePath );

    File[] getAttachedFiles( Plan plan );

    void save( Plan plan );

    boolean isReserved( String fileName );

    void remove( Plan plan, String url );

    void removeAttachment( Attachment attachment, Attachable attachable );

    void addAttachment( Attachment attachment, Attachable attachable );
}
