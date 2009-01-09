package com.mindalliance.channels.pages.profiles;

import org.apache.wicket.markup.html.WebPage;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 7, 2009
 * Time: 1:33:02 PM
 */
public class ProfileNotFoundPage extends WebPage {

    private String message;

    public ProfileNotFoundPage( String message ) {
        super();
        this.message = message;
        init();
    }

    private void init() {
        // build page elements
    }
}
