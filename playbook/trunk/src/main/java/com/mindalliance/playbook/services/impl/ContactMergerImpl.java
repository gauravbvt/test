package com.mindalliance.playbook.services.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.services.ContactMerger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Basic contact merger implementation.
 * <p/>
 * Merging is done by finding the first contact with some matching key values (name, email, etc) that doesn't have any
 * different non-null values for properties.
 * <p/>
 * If merging cannot be done, the contact is simply added to the current account.
 */
@Service
public class ContactMergerImpl implements ContactMerger {

    private static final Logger LOG = LoggerFactory.getLogger( ContactMergerImpl.class );

    @Autowired
    private ContactDao contactDao;
    
    @Autowired
    private AccountDao accountDao;

    @Override
    public void merge( Contact newContact ) {

        // Try matching emails
        for ( Object email : newContact.getEmails() )
            for ( Contact oldContact : contactDao.findByEmail( email ) )
                if ( oldContact.isMergeableWith( newContact ) ) {
                    LOG.debug( "Merging {} in {}", newContact, oldContact );
                    oldContact.merge( newContact );
                    return;
                }

        // Try by name, family name etc.
        for ( Contact oldContact : contactDao.findByName( newContact ) )
            if ( oldContact.isMergeableWith( newContact ) ) {
                LOG.debug( "Merging {} in {}", newContact, oldContact );
                oldContact.merge( newContact );
                return;
                }

        accountDao.getCurrentAccount().addContact( newContact );
        LOG.debug( "Added {}", newContact );
    }

    @Override
    public void merge( List<Contact> contacts ) {
        for ( Contact contact : contacts )
            merge( contact );
        LOG.debug( "Merged {} contacts", contacts.size() );
    }
}
