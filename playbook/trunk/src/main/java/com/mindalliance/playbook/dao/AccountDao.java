/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.dao;

import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Medium;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Account manipulator.
 */
public interface AccountDao extends GenericDao<Account,Long> {

    /**
     * Find an account by its confirmation key.
     * @param key the key
     * @return an account or null if not found
     */
    @Transactional
    Account findByConfirmation( String key );

    /**
     * Get the account of the current user.
     * @return an account or null
     */
    @Transactional
    Account getCurrentAccount();

    /**
     * Return the user authentication details for an account.
     * @param account the account
     * @return the security information
     */
    UserDetails getDetails( Account account );

    /**
     * Find an account using a userId from a provider.
     * @param providerId the provider id
     * @param userId the user id
     * @return an account or null if not found
     */
    @Transactional
    Account findByUserId( String providerId, String userId );

    /**
     * Find an account by its user key.
     * @param userKey "{providerId}:{userId}"
     * @return the corresponding account, or null if not found
     */
    @Transactional
    Account findByUserKey( String userKey );

    /**
     * Set the current account for this thread.
     * @param account the account
     */
    void setCurrentAccount( Account account );

    /**
     * Find any contact in any account that has the same email address as a specific account.
     *
     *
     *
     * @param account the account
     * @return aliases in any other account, including this one.
     */
    @SuppressWarnings( "unchecked" )
    @Transactional
    Set<Contact> findAliases( Account account );

    /**
     * Find the first account that contains a playbook having a main contact containing an equivalent medium.
     * @param medium the medium to match
     * @return a matching account or null if none was found
     */
    @Transactional
    Account findByMedium( Medium medium );

    /**
     * Find an account matching one of a contact's key medium.
     * @param contact the contact
     * @return an account or null if one was found
     */
    @Transactional
    Account findByContact( Contact contact );
}
