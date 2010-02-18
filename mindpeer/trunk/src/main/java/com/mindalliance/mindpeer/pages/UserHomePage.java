// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.access.annotation.Secured;

import javax.servlet.http.HttpServletResponse;

/**
 * The user's personal homepage.
 */
public class UserHomePage extends WebPage {

    @SpringBean
    private UserDao userDao;

    /** User of displayed page. */
    private User displayedUser;

    /**
     * Create a new UserHomePage instance for current user.
     */
    public UserHomePage() {
        displayedUser = userDao.currentUser();
        init();
    }

    /**
     * Constructor which receives wrapped query string parameters for a request.
     * @param parameters
     *            Wrapped query string parameters.
     */
    public UserHomePage( PageParameters parameters ) {
        displayedUser = userDao.findByName( parameters.getString( "username" ) );
        init();
    }

    /**
     * Populate the page.
     */
    @Secured( { "ROLE_ADMIN", "USER==displayedUser" } )
    public void init() {
        if ( displayedUser == null )
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );

        add( new Label( "username", displayedUser.getUsername() ),
             new BookmarkablePageLink<Void>( "public-home", PublicHomePage.class ) );
    }

    /**
     * Return the page's displayed user.
     * @return the displayed user
     */
    public User getDisplayedUser() {
        return displayedUser;
    }
}
