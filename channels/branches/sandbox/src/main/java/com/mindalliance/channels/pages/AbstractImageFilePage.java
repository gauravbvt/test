package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Plan;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Response;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.AbortWithHttpStatusException;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 23, 2010
 * Time: 6:17:23 PM
 */
public abstract class AbstractImageFilePage extends Page {

    private String filename;

    public AbstractImageFilePage( PageParameters parameters ) {
        super( parameters );

        String fileNameParam = parameters.getString( "0" );
        if ( fileNameParam == null )
            throw new AbortWithWebErrorCodeException( 403 );
        File file;
        try {
            filename = decodeFileName( fileNameParam );
            file = getFile( filename );
        } catch ( IOException e ) {
            throw new AbortWithHttpStatusException( 404, false );
        }
        if ( file.exists() ) {
            Response response = getResponse();
            response.setContentLength( file.length() );
            response.setLastModifiedTime( Time.valueOf( file.lastModified() ) );
            WebApplication application = (WebApplication) getApplication();
            response.setContentType( application.getServletContext().getMimeType( filename ) );

        } else
            throw new AbortWithHttpStatusException( 404, false );

    }

    protected abstract String decodeFileName( String fileName );

    protected abstract File getFile( String fileName ) throws IOException;

    @Override
    protected void onRender( MarkupStream markupStream ) {
        FileInputStream inputStream = null;
        Logger logger = LoggerFactory.getLogger( getClass() );
        try {
            OutputStream outputStream = getResponse().getOutputStream();
            inputStream = new FileInputStream( getFile( filename ) );

            byte[] buffer = new byte[10 * 1024];
            int len = inputStream.read( buffer );
            while ( len != -1 ) {
                outputStream.write( buffer, 0, len );
                len = inputStream.read( buffer );
            }

        } catch ( FileNotFoundException e ) {
            // Should not happen... trapped with a 404 before...
            logger.warn( "Unable to download file", e );
        } catch ( IOException e ) {
            logger.warn( "Unable to download file", e );
        } finally {
            if ( inputStream != null )
                try {
                    inputStream.close();
                } catch ( IOException e ) {
                    logger.warn( "Unable to close downloaded file", e );
                }
        }
    }

    protected Plan getPlan() {
        return User.current().getPlan();
    }

}
