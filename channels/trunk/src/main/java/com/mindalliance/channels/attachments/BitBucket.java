package com.mindalliance.channels.attachments;

import com.mindalliance.channels.AttachmentManager;
import org.apache.wicket.markup.html.form.upload.FileUpload;

/**
 * A bogus document manager that drops everything.
 */
public class BitBucket implements AttachmentManager {

    public BitBucket() {
    }

    /**
      * {@inheritDoc}
      */
    public boolean exists( String url ) {
        return false;
    }

    /**
      * {@inheritDoc}
      */
    public Attachment upload( Attachment.Type selectedType, FileUpload upload ) {
        return null;
    }

    /**
      * {@inheritDoc}
      */
    public String getLabel( Attachment attachment ) {
        return null;
    }

}
