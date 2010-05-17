package com.mindalliance.channels.attachments;

import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Plan;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.io.File;

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

}
