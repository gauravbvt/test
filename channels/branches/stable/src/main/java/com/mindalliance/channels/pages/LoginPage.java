package com.mindalliance.channels.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Login authentication page.
 * Hooked up through WEB-INF/securityConfig.xml.
 */
public class LoginPage extends WebPage {

    public LoginPage( PageParameters parameters ) {
        super( parameters );
    }
}
