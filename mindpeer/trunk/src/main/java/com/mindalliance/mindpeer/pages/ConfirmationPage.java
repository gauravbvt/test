// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.servlet.http.HttpServletResponse;

/**
 * The page that officially welcomes and activates the user.
 * A link to this page was sent to the user at the end of the registration process.
 */
public class ConfirmationPage extends WebPage {

    @SpringBean
    private UserDao userDao;

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
    public ConfirmationPage( PageParameters parameters ) {
        confirm( parameters.getAsLong( "user" ), parameters.getAsLong( "confirmation" ) );
    }

    /**
     * Confirm a given user with a confirmation number.
     *
     * @param userId the given user's id
     * @param confirmation the given confirmation
     */
    private void confirm( Long userId, Long confirmation ) {
        confirm( userDao.load( userId ), confirmation );
    }

    /**
     * Confirm a given user with a confirmation number.
     *
     * @param user the given user
     * @param confirmation the given confirmation
     */
    private void confirm( User user, Long confirmation ) {

        if ( user == null || confirmation == null || !user.isConfirmableWith( confirmation ) )
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );

        user.setConfirmed( true );
        userDao.save( user );
    }
}
