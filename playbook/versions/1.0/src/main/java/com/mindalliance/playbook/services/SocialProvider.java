// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.services;

import com.mindalliance.playbook.model.Contact;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;

/**
 * A connection to a social provider.
 */
public interface SocialProvider {

    /**
     * Get the provider id for connections managed by this provider.
     * @return a string
     */
    String getProviderId();

    /**
     * Create a connection factory.
     *
     * @param registry the registry to register in
     */
    void registerFactory( ConnectionFactoryRegistry registry );

    /**
     * Merge all contacts from a connection into current account.
     * @param merger the merger service to use
     * @param connection the user provider connection
     */
    void mergeContacts( ContactMerger merger, Connection<?> connection );

    /**
     * Create a new contact from a provider user.
     *
     * @param connection the user provider connection
     * @param userId the user id on the provider
     * @return a new contact
     */
    Contact newContact( Connection<?> connection, String userId );

    /**
     * Test if this provider is currently enabled.
     * @return true if it is
     */
    boolean isEnabled();
}
