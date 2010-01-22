// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.model.IUser;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * The user's personal homepage.
 */
public class UserHomePage extends WebPage {

    @SpringBean
    private IUser user;

    public UserHomePage() {

        add( new Label( "username", user.getUsername() ),
             new BookmarkablePageLink<Void>( "public-home", PublicHomePage.class ) );
    }
}
