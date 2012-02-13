/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.dao;

import com.mindalliance.playbook.model.Account;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

/**
 * Account manipulator.
 */
@Transactional
public interface AccountDao extends GenericDao<Account,Long> {

    /**
     * Find an account given its email.
     * @param email the email address
     * @return an account or null if not found
     */
    Account findByEmail( String email );

    /**
     * Find an account by its confirmation key.
     * @param key the key
     * @return an account or null if not found
     */
    Account findByKey( String key );

    /**
     * Get the account of the current user.
     * @return an account or null
     */
    Account getCurrentAccount();

    /**
     * Return the user authentication details for an account.
     * @param account the account
     * @return the security information
     */
    UserDetails getDetails( Account account );
}
