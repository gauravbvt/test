package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.dao.User;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;

/**
 * Simple file uploader.
 */
public class UploadedImage extends DynamicImageResource {


    public UploadedImage(  ) {
        super( "png" );
    }

    @Override
    protected byte[] getImageData( Attributes attributes ) {
        PageParameters parameters = attributes.getParameters();
        String fileName = parameters.get( "name" ).toString();
        try {
            File imageFile = getFile( fileName );
            FileInputStream in = new FileInputStream( imageFile );
            return IOUtils.toByteArray( in );
        } catch ( Exception e ) {
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_NOT_FOUND, "Not found" );
        }
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof UploadedImage;
    }

    protected File getFile( String fileName ) {
        AttachmentManager attachmentManager = ((Channels)Channels.get()).getAttachmentManager();
        return new File( attachmentManager.getUploadDirectory( User.plan() ), fileName );
    }

}
