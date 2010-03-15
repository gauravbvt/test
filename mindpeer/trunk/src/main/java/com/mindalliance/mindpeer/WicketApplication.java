// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer;

import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import com.mindalliance.mindpeer.pages.AccountPage;
import com.mindalliance.mindpeer.pages.ConfirmationPage;
import com.mindalliance.mindpeer.pages.EditProfilePage;
import com.mindalliance.mindpeer.pages.FocusPage;
import com.mindalliance.mindpeer.pages.LoginPage;
import com.mindalliance.mindpeer.pages.ProductPage;
import com.mindalliance.mindpeer.pages.PublicHomePage;
import com.mindalliance.mindpeer.pages.RegistrationPage;
import com.mindalliance.mindpeer.pages.SearchResultPage;
import com.mindalliance.mindpeer.pages.UserDispatchPage;
import com.mindalliance.mindpeer.pages.UserListPage;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.coding.BookmarkablePageRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.StringTokenizer;

/**
 * Application object for your web application. If you want to run this application without
 * deploying, run the Start class.
 */
public class WicketApplication extends WebApplication {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private UserDao userDao;

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

        mount( new QueryStringUrlCodingStrategy( "register", RegistrationPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "login", LoginPage.class ) );

        mount( new QueryStringUrlCodingStrategy( "search", SearchResultPage.class ) );
        mount( new MixedParamUrlCodingStrategy( "confirm",
                    ConfirmationPage.class, new String[]{ "confirmation", "user" } ) );

        mount( new MixedParamUrlCodingStrategy( "focus",
                    FocusPage.class, new String[]{ "name", "section", "type" } ) );

        mount( new QueryStringUrlCodingStrategy( "admin", UserListPage.class ) );

        mount( new QueryStringUrlCodingStrategy( "account/profile", EditProfilePage.class ) );
        mount( new QueryStringUrlCodingStrategy( "account", AccountPage.class ) );
//        mountBookmarkablePage( "account/friends", FriendsPage.class );
//        mountBookmarkablePage( "account/subscriptions", SubcriptionPage.class );
//        mount( new MixedParamUrlCodingStrategy( "account/statements",
//                    StatementsPage.class, new String[]{ "month", "type" } ) );

        mount( new MixedParamUrlCodingStrategy( "products",
                    ProductPage.class, new String[]{ "name", "section", "type" } ) );

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

    /**
     * ...
     * @return IRequestCycleProcessor
     */
    @Override
    protected IRequestCycleProcessor newRequestCycleProcessor() {
        return new MPRequestCycleProcessor( userDao );
    }

    //============================================================
    /**
     * Fixup to process /{username} URLs.
     */
    private static class MPRequestCycleProcessor extends WebRequestCycleProcessor {

        private UserDao userDao;

        /**
         * Create a new MPRequestCycleProcessor instance.
         *
         * @param userDao the given userDao
         */
        private MPRequestCycleProcessor( UserDao userDao ) {
            this.userDao = userDao;
        }

        /**
         * ...
         * @return IRequestCodingStrategy
         */
        @Override
        protected IRequestCodingStrategy newRequestCodingStrategy() {
            return new MPRequestCodingStrategy( userDao );
        }
    }

    //============================================================
    private static class MPRequestCodingStrategy extends WebRequestCodingStrategy {

        @Autowired
        UserDao userDao;

        private final MPUrlCodingStrategy strategy = new MPUrlCodingStrategy();

        public MPRequestCodingStrategy( UserDao userDao ) {
            this.userDao = userDao;
        }

        @Override
        public IRequestTargetUrlCodingStrategy urlCodingStrategyForPath( String path ) {
            IRequestTargetUrlCodingStrategy target = super.urlCodingStrategyForPath( path );
            if ( target != null ) {
                return target;
            } else {
                int i = path.indexOf( '/' );
                String name = i == -1 ? path : path.substring( 0, i );
                if ( userDao.findByName( name ) == null )
                    return null;
                else
                    return strategy;
            }
        }
    }

    //============================================================
    private static class MPUrlCodingStrategy
            extends BookmarkablePageRequestTargetUrlCodingStrategy {

        /**
         * Create a new CmsUrlCodingStrategy instance.
         */
        private MPUrlCodingStrategy() {
            super( "/", UserDispatchPage.class, null );
        }

        /**
         * ...
         *
         * @param requestParameters the given requestParameters
         * @return IRequestTarget
         */
        @Override
        public IRequestTarget decode( RequestParameters requestParameters ) {

            PageParameters params = new PageParameters();
            String path = requestParameters.getPath();
            int i = 0;
            for ( StringTokenizer t = new StringTokenizer( path, "/" ); t.hasMoreTokens(); )
                params.put( Integer.toString( i++ ), t.nextToken() );

            return new BookmarkablePageRequestTarget(
                    i == 0 ? PublicHomePage.class : UserDispatchPage.class,
                    params );
        }
    }
}
