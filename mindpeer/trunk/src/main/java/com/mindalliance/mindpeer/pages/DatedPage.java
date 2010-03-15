// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.pages;

import org.apache.wicket.PageParameters;

/**
 * A page that has a last-modified date that depends on its content.
 */
public interface DatedPage {

    /**
     * Get the last-modified time for this page given some parameters.
     * @param parms the page parameters.
     * @return the last modification time or -1 if page should be recreated
     */
    long getLastModified( PageParameters parms );

}
