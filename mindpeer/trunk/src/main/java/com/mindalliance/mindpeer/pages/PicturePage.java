// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.dao.ProfileDao;
import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.Profile;
import com.mindalliance.mindpeer.model.User;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Rendering of picture attached to a profile.
 */
public class PicturePage extends Page implements DatedPage {

    @SpringBean
    private UserDao userDao;

    @SpringBean
    private ProfileDao profileDao;

    private Profile profile;

    private static final Logger LOG = LoggerFactory.getLogger( PicturePage.class );

    private static final String USERNAME_PARM = "0";

    public PicturePage( PageParameters parameters ) {
        super( parameters );
        setStatelessHint( true );

        User user = userDao.findByName( parameters.getString( USERNAME_PARM ) );
        if ( user == null )
            throw new AbortWithWebErrorCodeException( SC_NOT_FOUND );

        profile = user.getProfile();
    }

    @Override
    protected void onRender( MarkupStream markupStream ) {
        // Get the response and application
        RequestCycle cycle = getRequestCycle();
        Response response = cycle.getResponse();

        // Set content type based on markup type for page
        response.setContentType("image/png");
        response.setLastModifiedTime( Time.milliseconds( profile.getLastModified().getTime() ) );

        OutputStream outputStream = response.getOutputStream();
        try {
            byte[] pic = profile.getPicture();
            outputStream.write( pic == null ? getDefaultPic() : pic );

        } catch ( IOException e ) {
            LOG.error( "Unable to send picture bytes", e );
        }
    }

    private byte[] getDefaultPic() throws IOException {
        InputStream stream = getClass().getResourceAsStream( "defaultPic.png" );

        byte[] result = new byte[ stream.available() ];
        stream.read( result );

        stream.close();
        return result;
    }

    /**
     * Get the last-modified time for this page given some parameters.
     * @param parms the page parameters.
     * @return the last modification time or -1 if page should be recreated
     */
    public long getLastModified( PageParameters parms ) {
        return userDao.findByName( parms.getString( USERNAME_PARM ) ).getLastModified().getTime();
    }
}
