package com.mindalliance.channels.attachments;

import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.io.File;
import java.util.List;

/**
 * A bogus document manager that drops everything.
 */
public class BitBucket implements AttachmentManager {

    public BitBucket() {
    }

    /**
      * {@inheritDoc}
      */
    public boolean exists( Plan plan, String url ) {
        return false;
    }

    /**
      * {@inheritDoc}
      */
    public Attachment upload(
            Plan plan, Attachment.Type selectedType, String name, FileUpload upload ) {
        return null;
    }

    /**
      * {@inheritDoc}
      */
    public String getLabel( Plan plan, Attachment attachment ) {
        return null;
    }

    /**
      * {@inheritDoc}
      */
    public void removeUnattached( PlanDao planDao ) {
        // Do nothing
    }

    /**
      * {@inheritDoc}
     * @param plan
     */
    public File getUploadDirectory( Plan plan ) {
        return null;
    }

    /**
      * {@inheritDoc}
      */
    public String getUploadPath() {
        return null;
    }

    /**
      * {@inheritDoc}
      */
    public boolean hasVideoContent( String url ) {
        return false;
    }

    /**
      * {@inheritDoc}
      */
    public boolean hasImageContent( String url ) {
        return false;
    }

    /**
     * Get all media reference attachments.
     *
     * @param object
     * @return a list of attachments
     */
    @SuppressWarnings( "unchecked" )
    public List<Attachment> getMediaReferences( ModelObject object ) {
        return (List<Attachment>) CollectionUtils.select(
                object.getAttachments(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return isMediaReference( ( (Attachment) object ) );
                    }
                }
        );
    }

    /**
     * Whether the attchment is an image or video reference.
     *
     * @param attachment
     * @return a boolean
     */
    public boolean isMediaReference( Attachment attachment ) {
        return isImageReference( attachment ) || isVideoReference( attachment );
    }

    /**
     * Whether the attachment is a reference movie.
     *
     * @param attachment
     * @return a boolean
     */
    public boolean isVideoReference( Attachment attachment ) {
        return attachment.getType() == Attachment.Type.Reference && hasVideoContent( attachment.getUrl() );
    }

    /**
     * Whether the attachment is a reference image.
     *
     * @param attachment
     * @return a boolean
     */
    public boolean isImageReference( Attachment attachment ) {
        return attachment.getType() == Attachment.Type.Reference && hasImageContent( attachment.getUrl() );
    }

    @Override
    public boolean isUploadedFileDocument( String url ) {
        return false;
    }

    @Override
    public File getUploadedFile( String planRelativePath ) {
        return null;
    }

}
