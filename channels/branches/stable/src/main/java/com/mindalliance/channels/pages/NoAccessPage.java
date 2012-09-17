// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Shown to non-planner users when no production plans are available on the server.
 */
public class NoAccessPage extends WebPage {

    /** Pages are serialized. */
    private static final long serialVersionUID = 5605572315565102900L;

    /** Current user. */
    @SpringBean
    private ChannelsUser user;

    public NoAccessPage() {
        add(
            new Label( "loggedUser", user.getUsername() ) );        
    }
}