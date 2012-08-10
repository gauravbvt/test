package com.mindalliance.playbook.dao;

import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Medium;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Contact accessor.
 */
@Secured( "ROLE_USER" )
public interface ContactDao extends IndexedDao<Contact,Long> {

    /**
     * Find a contact by parts of name. If all arguments are null or empty, this will return an empty collection.
     * 
     * @param givenName the first name
     * @param additionalNames middle initials or other names
     * @param familyName the last name
     * @param suffixes suffixes
     * @return matching contacts
     */
    @Transactional
    List<Contact> findByName( String givenName, String additionalNames, String familyName, String suffixes );

    /**
     * Find contacts with similar names.
     * @param contact a contact to look for
     * @return list of potential synonyms
     */
    @Transactional
    List<Contact> findByName( Contact contact );

    /**
     * Make sure contact is present in current account. 
     * If not, copy information relevant to a collaboration.
     *
     * @param foreignContact the foreign contact
     * @param collaboration the collaboration
     * @return a private contact, possibly new
     */
    @Transactional
    Contact privatize( Contact foreignContact, Collaboration collaboration );

    /**
     * Find contacts with a specified medium.
     * @param medium the mediumm
     * @return some local contacts of current user
     */
    @SuppressWarnings( "unchecked" )
    List<Contact> findByMedium( Medium medium );
}
