// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer;

import com.mindalliance.mindpeer.model.User;
import com.mindalliance.mindpeer.pages.ConfirmationPage;
import com.mindalliance.mindpeer.pages.LoginPage;
import com.mindalliance.mindpeer.pages.PublicHomePage;
import com.mindalliance.mindpeer.pages.RegistrationPage;
import com.mindalliance.mindpeer.pages.UserHomePage;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Application object for your web application. If you want to run this application without
 * deploying, run the Start class.
 */
public class WicketApplication extends WebApplication {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Create a new WicketApplication instance.
     */
    public WicketApplication() {
    }

    /**
     * Register page entry points.
     */
    @Override
    public void init() {
        super.init();
        getMarkupSettings().setStripWicketTags( true );

        mountBookmarkablePage( "register.html", RegistrationPage.class );
        mountBookmarkablePage( "login.html", LoginPage.class );

        mount( new MixedParamUrlCodingStrategy( "user", UserHomePage.class,
                                                new String[]{ "username" } ) );

        mount( new MixedParamUrlCodingStrategy( "confirm", ConfirmationPage.class,
                                                new String[]{ "confirmation", "user" } ) );

        addComponentInstantiationListener(
                new SpringComponentInjector( this, applicationContext ) );
    }

    /**
     * Return the WicketApplication's homePage.
     * @return the value of homePage
     */
    @Override
    public Class<? extends Page> getHomePage() {
        return PublicHomePage.class;
    }

    /**
     * Return the link for activating a particular user.
     *
     * @param user the given user
     * @return the confirmation URL relative to the webapp context path
     */
    public CharSequence getConfirmationURL( User user ) {
        IRequestCodingStrategy strategy = getRequestCycleProcessor().getRequestCodingStrategy();

        PageParameters parms = new PageParameters();
        if ( user != null ) {
            parms.add( "user", Long.toString( user.getId() ) );
            parms.add( "confirmation", Long.toString( user.getConfirmation() ) );
        }

        return strategy.pathForTarget(
            new BookmarkablePageRequestTarget( ConfirmationPage.class, parms ) );
    }
}
