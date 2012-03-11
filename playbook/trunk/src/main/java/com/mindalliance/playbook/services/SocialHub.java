// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.services;

import com.mindalliance.playbook.model.Account;
import org.springframework.scheduling.annotation.Async;
import org.springframework.social.connect.Connection;
import org.springframework.transaction.annotation.Transactional;

/**
 * Connector to external providers.
 */
public interface SocialHub {

    /**
     * Test if this installation has been configured for Facebook support.
     * @return true if users can login through Facebook
     */
    boolean isFacebookEnabled();

    /**
     * Create a new account after successful authentication from a provider when no matching account was found.
     * This is a callback from the OAuth authentication process.
     *
     * @param connection the provider connection
     * @return the new account to associate with this user
     */
    @Transactional
    Account signUp( Connection<?> connection );

    /**
     * Test if LinkedIn connections are enabled on this server.
     * @return true if they are
     */
    boolean isLinkedInEnabled();

    /**
     * Test if Twitter connections are enabled on this server.
     * @return true if they are
     */
    boolean isTwitterEnabled();

    /**
     * Synchronize all contacts from an account.
     * @param account the account
     */
    @Async
    void syncContacts( Account account );

    /**
     * Synchronize contacts for one connection of an account.
     * @param account the user account
     * @param connection the provider client connection
     */
    void syncContacts( Account account, Connection<?> connection );
}
