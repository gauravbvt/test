// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.PageParameters;

/**
 * The public home page.
 */
public class PublicHomePage extends WebPage {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor that is invoked when page is invoked without parameters.
     */
    public PublicHomePage() {

        // Add links
        add( new BookmarkablePageLink<Void>( "home",
                FocusPage.class, new PageParameters( "id=all" ) )
            );
    }
}
