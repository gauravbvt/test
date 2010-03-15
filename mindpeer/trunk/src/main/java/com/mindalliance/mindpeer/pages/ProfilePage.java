// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.PageParameters;

/**
 * A read-only profile information page.
 */
public class ProfilePage extends WebPage {

    /**
     * Constructor which receives wrapped query string parameters for a request. Having this
     * constructor public means that your page is 'bookmarkable' and hence can be called/ created
     * from anywhere. For bookmarkable pages (as opposed to when you construct page instances
     * yourself, this constructor will be used in preference to a no-arg constructor, if both exist.
     * Note that nothing is done with the page parameters argument. This constructor is provided so
     * that tools such as IDEs will include it their list of suggested constructors for derived
     * classes.
     *
     * Please call this constructor (or the one with the pagemap) if you want to remember the
     * pageparameters {@link #getPageParameters()}. So that they are reused for stateless links.
     *
     * @param parameters
     *            Wrapped query string parameters.
     */
    public ProfilePage( PageParameters parameters ) {
        super( parameters );
    }
}
