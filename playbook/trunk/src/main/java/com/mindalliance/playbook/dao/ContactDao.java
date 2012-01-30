package com.mindalliance.playbook.dao;

import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Contact;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Contact accessor.
 */
@Secured( "ROLE_USER" )
public interface ContactDao extends IndexedDao<Contact,Long> {

    /**
     * Find contacts with a given email.
     * 
     * @param email the email
     * @return all contacts using this email
     */
    
    @Transactional( readOnly = true )
    List<Contact> findByEmail( Object email );

    /**
     * Find a contact by parts of name. If all arguments are null or empty, this will return an empty collection.
     * 
     * @param givenName the first name
     * @param additionalNames middle initials or other names
     * @param familyName the last name
     * @param suffixes suffixes
     * @return matching contacts
     */
    @Transactional( readOnly = true )
    List<Contact> findByName( String givenName, String additionalNames, String familyName, String suffixes );

    /**
     * Find contacts with similar names.
     * @param contact a contact to look for
     * @return list of potential synonyms
     */
    @Transactional( readOnly = true )
    List<Contact> findByName( Contact contact );
    
    /**
     * Find any contact in any account that has the same email address as a specific account.
     * @param account the account
     * @return aliases in any other account, including this one.
     */
    @Transactional( readOnly = true )
    List<Contact> findAliases( Account account );
    
}
