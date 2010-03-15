// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages;

import org.apache.wicket.PageParameters;

/**
 * The user's account information and setup page.
 */
public class AccountPage extends AbstractUserPage {

    /**
     * Create a new MindPeerPage instance.
     *
     * @param parameters the given parameters
     */
    public AccountPage( PageParameters parameters ) {
        super( parameters );
    }

    /**
     * Return the MindPeerPage's title.
     * @return the value of title
     */
    @Override
    public String getTitle() {
        return "Account";
    }

    /**
     * Return the MindPeerPage's selectedTopItem.
     * @return the value of selectedTopItem
     */
    @Override
    protected int getSelectedTopItem() {
        return 2;
    }
}
