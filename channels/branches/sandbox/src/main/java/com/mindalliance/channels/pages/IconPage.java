package com.mindalliance.channels.pages;

import com.mindalliance.channels.imaging.ImagingService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.File;
import java.io.IOException;

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

    protected File getFile( String fileName ) throws IOException {
        return new File( fileName );
    }

}
