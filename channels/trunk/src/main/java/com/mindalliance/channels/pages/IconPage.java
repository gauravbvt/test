package com.mindalliance.channels.pages;

import com.mindalliance.channels.engine.imaging.ImagingService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Icon page.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 23, 2010
 * Time: 6:12:07 PM
 */
public class IconPage extends AbstractImageFilePage {
    @SpringBean
    private ImagingService imagingService;

    public IconPage( PageParameters parameters ) {
        super( parameters );
    }

    protected String decodeFileName( String fileName ) {
        try {
            int i = fileName.lastIndexOf( "||" );
            String iconName = i >=0 ? fileName.substring( i + 2 ) : fileName;
            String prefix = i >=0 ? fileName.substring( 0, i ) : "";
            return prefix + "||" + URLEncoder.encode( iconName, "UTF-8" ); // We actually want to UNDO the automatic URL decoding done by Wicket.
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( e ); // Will never happen
        }
    }

    protected File getFile( String encodedPath ) throws IOException {
        return imagingService.findIcon(  encodedPath );
    }

}
