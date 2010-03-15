// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.pages;

import org.apache.wicket.PageParameters;

/**
 * Focus list management page.
 */
public class FocusListPage extends AbstractUserPage {

    private static final long serialVersionUID = 7922494749177686302L;

    /**
     * Create a new MindPeerPage instance.
     *
     * @param parameters the given parameters
     */
    public FocusListPage( PageParameters parameters ) {
        super( parameters );
    }

    /**
     * Return the page's title.
     * @return the value of title
     */
    @Override
    public String getTitle() {
        return "Focus list";
    }

    /**
     * Return the page's selectedTopItem.
     * @return the value of selectedTopItem
     */
    @Override
    protected int getSelectedTopItem() {
        return 0;
    }
}
