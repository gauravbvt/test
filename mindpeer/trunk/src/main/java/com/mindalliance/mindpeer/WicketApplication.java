// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer;

import com.mindalliance.mindpeer.pages.RegisterPage;
import com.mindalliance.mindpeer.pages.UserHomePage;
import com.mindalliance.mindpeer.pages.PublicHomePage;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

/**
 * Application object for your web application. If you want to run this application without
 * deploying, run the Start class.
 *
 * @author Richard Wilkinson - richard.wilkinson@jweekend.com
 *
 */
public class WicketApplication extends WebApplication {

    /**
     * Create a new WicketApplication instance.
     */
    public WicketApplication() {
    }

    /**
     * Return the WicketApplication's springInjector.
     * @return the value of springInjector (the component responsible of filling up @SpringBean
     * properties)
     */
    protected SpringComponentInjector getSpringInjector() {
        return new SpringComponentInjector( this );
    }

    /**
     * Register page entry points.
     */
    @Override
    protected void init() {
        super.init();

        mountBookmarkablePage( "home.html", UserHomePage.class );
        mountBookmarkablePage( "register.html", RegisterPage.class );

        addComponentInstantiationListener( getSpringInjector() );
    }

    /**
     * Return the WicketApplication's homePage.
     * @return the value of homePage
     */
    @Override
    public Class<? extends Page> getHomePage() {
        return PublicHomePage.class;
    }
}
