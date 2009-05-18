package com.mindalliance.channels.attachments;

import com.mindalliance.channels.AttachmentManager;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A bogus attachment manager that drops everything.
 */
public class BitBucket implements AttachmentManager {

    public BitBucket() {
    }

    /**
      * {@inheritDoc}
      */
    public Attachment getAttachment( String ticket ) {
        return null;
    }

    public List<Attachment> getAttachments( List<String> attachmentTickets ) {
        return new ArrayList<Attachment>();
    }

    /**
     * {@inheritDoc}
     */
    public String attach( Attachment.Type type, FileUpload fileUpload, List<String> tickets ) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String attach( Attachment.Type type, String url, String digest, List<String> attachmentTickets ) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String attach( Attachment.Type type, URL url, List<String> tickets ) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void detach( String ticket ) {
    }

    /**
     * {@inheritDoc}
     */
    public void detachAll( List<String> tickets ) {
    }

    /**
     * {@inheritDoc}
     */
    public Attachment reattach( String ticket ) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void reattachAll( List<String> tickets ) {

    }

    /**
     * {@inheritDoc}
     */
    public File findUploaded( String url, String digest ) {
        return null;
    }

    /**
      * {@inheritDoc}
      */
    public void emptyTrash() {
    }
}
