package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.png.ChannelsDynamicImageResource;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Simple file uploader.
 */
public class UploadedImage extends ChannelsDynamicImageResource {


    static final List<String> IMAGE_EXTENSIONS = Arrays.asList( "png", "jpg", "jpeg", "bmp", "gif", "tif", "tiff", "svg" );

    public UploadedImage(  ) {
        super(  );
    }

    @Override
    protected byte[] getImageData( Attributes attributes ) {
        PageParameters parameters = attributes.getParameters();
        String fileName = parameters.get( "name" ).toString();
        String extension = FilenameUtils.getExtension( fileName ).toLowerCase();
        try {
            if ( extension.isEmpty() || !IMAGE_EXTENSIONS.contains( extension ) ) {
               throw new Exception( fileName + " is not an image file" );
            }
            setFormat( extension );
            File imageFile = getFile( fileName, parameters );
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

    protected File getFile( String fileName, PageParameters parameters ) {
        AttachmentManager attachmentManager = ((Channels)Channels.get()).getAttachmentManager();
        return new File( attachmentManager.getUploadDirectory( getPlan( parameters ) ), fileName );
    }

    private Plan getPlan( PageParameters parameters ) {
        Plan plan;
        if ( parameters.getNamedKeys().contains( AbstractChannelsWebPage.PLAN_PARM ) ) {
            ChannelsUser  user = ChannelsUser.current( getUserDao() );
            plan = AbstractChannelsWebPage.getPlanFromParameters( getPlanManager(), user, parameters );
        } else {
            plan = ChannelsUser.plan();
        }
        return plan;
    }

}
