package com.mindalliance.channels.pages.png;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.util.io.ByteArrayOutputStream;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Uploaded resource.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 8/6/12
 * Time: 11:56 AM
 */
public class UploadedResource extends AbstractResource {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( UploadedResource.class );

    private String uploadsDirPath;

    public UploadedResource( String uploadsDirPath ) {
        super();
        this.uploadsDirPath = uploadsDirPath;
    }

    @Override
    protected ResourceResponse newResourceResponse( Attributes attributes ) {
        final ResourceResponse resourceResponse = new ResourceResponse();
        final String filePath = getFilePath( attributes );
        final FileResourceStream fileResourceStream =
                new FileResourceStream( new File( filePath ) );
        resourceResponse.setContentType( fileResourceStream.getContentType() );
        resourceResponse.setLastModified( fileResourceStream.lastModifiedTime() );
        resourceResponse.setFileName( filePath );
        resourceResponse.setWriteCallback( new WriteCallback() {
            @Override
            public void writeData( final Attributes attributes ) {
                InputStream inputStream = null;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    inputStream = fileResourceStream.getInputStream();
                    Streams.copy( inputStream, baos );
                } catch ( ResourceStreamNotFoundException rsnfx ) {
                    LOG.warn( "Resource stream not found ", rsnfx.getMessage() );
                } catch ( IOException iox ) {
                    LOG.warn( "IO exception ", iox.getMessage() );
                } finally {
                    attributes.getResponse().write( baos.toByteArray() );
                    IOUtils.closeQuietly( inputStream );
                    IOUtils.closeQuietly( baos );
                }
            }
        } );

        return resourceResponse;
    }

    private String getFilePath( Attributes attributes ) {
        PageParameters parameters = attributes.getParameters();
        String fileName = parameters.get( "name" ).toString();
        return uploadsDirPath + File.separator + fileName;
    }
}
