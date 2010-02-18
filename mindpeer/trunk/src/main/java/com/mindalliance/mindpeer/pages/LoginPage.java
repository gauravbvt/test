// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.pages;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.protocol.http.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * The standard login page.
 */
public class LoginPage extends WebPage {

    public static final String ERROR_PARAMETER_NAME = "login_error";

    /**
     * Create a new LoginPage instance.
     */
    public LoginPage() {
        Object exception = getSessionAttribute( "SPRING_SECURITY_LAST_EXCEPTION" );
        add( exception == null ?
                              new WebMarkupContainer( "error" ).setVisible( false ) :
                              new Label( "error", ( (Throwable) exception ).getMessage() ),

             new BookmarkablePageLink<Void>( "signup", RegistrationPage.class ) );
    }

    /**
     * Get an object straight from the HTTP session.
     *
     * @param attribute of type String
     * @return Object the session object (or null)
     */
    private Object getSessionAttribute( String attribute ) {
        HttpServletRequest servletRequest = ( (WebRequest) getRequest() ).getHttpServletRequest();
        HttpSession session = servletRequest.getSession( false );

        return session == null ? null
                               : session.getAttribute( attribute );
    }
}
