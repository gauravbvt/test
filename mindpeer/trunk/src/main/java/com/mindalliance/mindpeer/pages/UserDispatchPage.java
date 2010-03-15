// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.dao.FocusDao;
import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A switch that forwards to the appropriate subpages of /users/{user_id}/...
 */
public class UserDispatchPage extends WebPage {

    public static final Set<String> Reserved = Collections.unmodifiableSet(
            new HashSet<String>( Arrays.asList(
                "admin", "account", "focus", "products", "resources", "search", "static"
    ) ) );


    @SpringBean
    private UserDao userDao;

    @SpringBean
    private FocusDao focusDao;

    private String parm;

    /**
     * Constructor which receives wrapped query string parameters for a request.
     *
     * Please call this constructor (or the one with the pagemap) if you want to remember the
     * pageparameters {@link #getPageParameters()}. So that they are reused for stateless links.
     *
     * @param parameters Wrapped query string parameters.
     */
    public UserDispatchPage( PageParameters parameters ) {
        super( parameters );

        if ( parameters.containsKey( "0" ) ) {
            User user = userDao.findByName( parameters.getString( "0" ) );
            if ( user != null && user.isEnabled() )
                parm1( parameters );
            else
                throw new AbortWithWebErrorCodeException( SC_NOT_FOUND );

        } else
            setResponsePage( PublicHomePage.class );
    }

    private void parm1( PageParameters parameters ) {
        if ( parameters.containsKey( "1" ) ) {
            parm = parameters.getString( "1" );

            if ( parm.equals( "avatar.png" ) )
                setResponsePage( PicturePage.class, parameters );

//            else if ( parm.equals( "vcard.vcf" ) )
//                setResponsePage( VCardPage.class, parameters );

            else
                throw new AbortWithWebErrorCodeException( SC_NOT_FOUND );

        } else
            setResponsePage( ProfilePage.class );
    }
}
