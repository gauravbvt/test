package com.mindalliance.channels;

import com.mindalliance.channels.attachments.Attachment;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.net.URL;
import java.util.List;

/**
 * A thing that keeps track of the associations of model objects and their file attachments.
 */
public interface AttachmentManager {
    /**
     * Get attachment given ticket.
     * @param ticket a string
     * @return an attachment
     */
    Attachment getAttachment( String ticket );

    /**
     * Get all attachments given a list of tickets.
     * @param attachmentTickets a list of strings
     * @return a list of attachments
     */
    List<Attachment> getAttachments( List<String> attachmentTickets );

    /**
     * Attach a file to an object.
     *
     * @param type       the type of the attachment
     * @param fileUpload the thing
     * @return a ticket
     */
    String attach( Attachment.Type type, FileUpload fileUpload );

    /**
     * Attach an URL to an object.
     *
     * @param type the type of the attachment
     * @param url  the URL
     * @return a ticket
     */
    String attach( Attachment.Type type, URL url );

    /**
     * Detach an attachment given its ticket. Should not complain if the attachment is not
     * actually attached.
     *
     * @param ticket a string
     */
    void detach( String ticket );

    /**
     * Detach all attachments mapped to tickets.
     *
     * @param tickets a list of tickets
     */
    void detachAll( List<String> tickets );

    /**
     * Reattach trashed attachment given its ticket.
     *
     * @param ticket a ticket
     * @return the reattached attachment
     */
    Attachment reattach( String ticket );

    /**
     * Reattach all trashed attachment mapped to given tickets.
     *
     * @param tickets a list of tickets
     */
    void reattachAll( List<String> tickets );

    /**
     * Irretrievably remove detached documents.
     */
    void emptyTrash();

}
