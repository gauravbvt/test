package com.mindalliance.channels.pages.png;


import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.pages.Channels;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/25/12
 * Time: 1:44 PM
 */
public class IconPng extends DynamicImageResource {

    protected IconPng() {
        super( "png" );
    }

    @Override
    protected byte[] getImageData( Attributes attributes ) {
        ImagingService imagingService = ((Channels)Channels.get()).getImagingService();
        PageParameters parameters = attributes.getParameters();
        String encodedPath = parameters.get( "name" ).toString();
        try {
            File iconFile = imagingService.findIcon( ChannelsUser.plan(), encodedPath );
            FileInputStream in = new FileInputStream( iconFile );
            return IOUtils.toByteArray( in );
        } catch ( Exception e ) {
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_NOT_FOUND, "Not found" );
        }
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof IconPng;
    }

}
