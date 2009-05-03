package com.mindalliance.channels.attachments;

import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.net.URL;

/**
 * A bogus attachment manager that drops everything.
 */
public class BitBucket implements AttachmentManager {

    /** Empty list to iterate on. */
    private List<Attachment> attachments = Collections.emptyList();

    public BitBucket() {
    }

    /** {@inheritDoc} */
    public Iterator<Attachment> attachments( long id ) {
        return attachments.iterator();
    }

    /** {@inheritDoc} */
    public void attach( long id, Attachment.Type type, FileUpload fileUpload ) {
    }

    /** {@inheritDoc} */
    public void attach( long id, Attachment.Type type, URL url ) {
    }

    /** {@inheritDoc} */
    public void detach( long id, Attachment attachment ) {
    }

    /** {@inheritDoc} */
    public void detachAll( long id ) {
    }
}
