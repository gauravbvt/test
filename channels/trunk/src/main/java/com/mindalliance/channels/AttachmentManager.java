package com.mindalliance.channels;

import com.mindalliance.channels.attachments.Document;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * A thing that keeps track of the associations of model objects and their file attachments.
 */
public interface AttachmentManager {
    /**
     * Get document given ticket.
     *
     * @param ticket a string
     * @return a document
     */
    Document getDocument( String ticket );

    /**
     * Get all documents given a list of tickets.
     *
     * @param attachmentTickets a list of strings
     * @return a list of documents
     */
    List<Document> getDocuments( List<String> attachmentTickets );

    /**
     * Attach a file to an object.
     *
     * @param type       the type of the attachment
     * @param fileUpload the thing
     * @param tickets    tickets of the model object to which document is to be attached
     * @return a ticket or null if the attachment would be redundant
     */
    String attach( Document.Type type, FileUpload fileUpload, List<String> tickets );

    /**
     * Attach already uploaded file.
     *
     * @param type              attachment type
     * @param url               url string of uploaded file
     * @param digest            the file's SHA digest
     * @param attachmentTickets model object's current tickets
     * @return an attachment ticket or null if attachment already exists
     */
    String attach( Document.Type type, String url, String digest, List<String> attachmentTickets );

    /**
     * Attach an URL to an object.
     *
     * @param type    the type of the attachment
     * @param url     the URL
     * @param tickets tickets of the model object to which document is to be attached
     * @return a ticket
     */
    String attach( Document.Type type, URL url, List<String> tickets );

    /**
     * Detach an attachment given its ticket. Should not complain if the document is not
     * actually attached.
     *
     * @param ticket a string
     */
    void detach( String ticket );

    /**
     * Detach all documents mapped to tickets.
     *
     * @param tickets a list of tickets
     */
    void detachAll( List<String> tickets );

    /**
     * Reattach trashed document given its ticket.
     *
     * @param ticket a ticket
     * @return the reattached document
     */
    Document reattach( String ticket );

    /**
     * Reattach all trashed documents mapped to given tickets.
     *
     * @param tickets a list of tickets
     */
    void reattachAll( List<String> tickets );

    /**
     * Find already uploaded file given its url and digest.
     *
     * @param url    a url string
     * @param digest a digest
     * @return a file or null if not found
     */
    File findUploaded( final String url, final String digest );

    /**
     * Irretrievably remove detached documents.
     */
    void emptyTrash();

}
