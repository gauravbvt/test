package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.dao.user.UserUploadService;
import com.mindalliance.channels.pages.Channels;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/18/12
 * Time: 10:01 AM
 */
public class UserPhotoPng extends ChannelsDynamicImageResource {

    protected UserPhotoPng() {
        super( "png" );
    }

    @Override
    protected byte[] getImageData( Attributes attributes ) {
        UserUploadService userUploadService = ( (Channels) Channels.get() ).getUserUploadService();
        PageParameters parameters = attributes.getParameters();
        String photoName = parameters.get( "name" ).toString();
        try {
            File photoFile = userUploadService.findSquaredUserPhoto( photoName );
            if ( photoFile != null ) {
                FileInputStream in = new FileInputStream( photoFile );
                return IOUtils.toByteArray( in );
            } else {
                throw new Exception( "Not found" );
            }
        } catch ( Exception e ) {
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_NOT_FOUND, "Not found" );
        }
    }

    @Override
    public boolean equals( Object that ) {
        return that instanceof UserPhotoPng;
    }

}
