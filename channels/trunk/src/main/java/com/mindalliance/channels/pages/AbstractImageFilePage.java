package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.util.ResponseOutputStream;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.BufferedWebResponse;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
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
    
    @SpringBean
    private ChannelsUser user;

    public AbstractImageFilePage( PageParameters parameters ) {
        super( parameters );

        String fileNameParam = parameters.get( "0" ).toString();
        if ( fileNameParam == null )
            throw new AbortWithHttpErrorCodeException( 403, "Unauthorized access" );
        File file;
        try {
            filename = decodeFileName( fileNameParam );
            file = getFile( filename );
        } catch ( IOException e ) {
            throw new AbortWithHttpErrorCodeException( 404, "Not found" );
        }
        if ( file.exists() ) {
            BufferedWebResponse response = new BufferedWebResponse(  (WebResponse) getResponse()  );
            response.setContentLength( file.length() );
            response.setLastModifiedTime( Time.millis( file.lastModified() )  );
            WebApplication application = (WebApplication) getApplication();
            response.setContentType( application.getServletContext().getMimeType( filename ) );

        } else
            throw new AbortWithHttpErrorCodeException( 404, "Not found" );

    }

    protected abstract String decodeFileName( String fileName );

    protected abstract File getFile( String fileName ) throws IOException;

    @Override
    protected void onRender(  ) {
        FileInputStream inputStream = null;
        Logger logger = LoggerFactory.getLogger( getClass() );
        try {
            OutputStream outputStream = new ResponseOutputStream( getResponse() );
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

    protected CollaborationModel getPlan() {
        return user.getCollaborationModel();
    }

}
