package com.mindalliance.playbook.services;

import com.mindalliance.playbook.model.Contact;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Contact merging service.
 */
@Secured( "ROLE_USER" )
public interface ContactMerger {

    /**
     * Add a contact to the current account. If a similar contact already exists, the old contact will be updated with
     * the information of the new contact.
     *
     * @param newContact a new contact
     */
    @Transactional
    void merge( Contact newContact );

    /**
     * Merge a list of potentially new contacts into existing ones.
     * @param contacts a list of contacts
     */
    @Transactional
    void merge( List<Contact> contacts );

    /**
     * Import a stream of VCards.
     * 
     * @param inputStream  the stream
     * @throws IOException on errors
     */
    @Transactional
    void importVCards( InputStream inputStream ) throws IOException;
}
