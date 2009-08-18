package com.mindalliance.channels.imaging;

import com.mindalliance.channels.ImagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Default imaging service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 18, 2009
 * Time: 1:48:49 PM
 */
public class DefaultImagingService implements ImagingService {

    /**
     * The directory to keep files in.
     */
    private Resource directory;
    /**
     * The webapp-relative path to file URLs.
     */
    private String path = "";

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultImagingService.class );

    public DefaultImagingService() {
    }

    public synchronized void setPath( String path ) {
        this.path = path;
    }

    public void setDirectory( Resource directory ) {
        this.directory = directory;
    }

    public int[] getImageSize( String url ) {
        int[] size = new int[2];
        try {
            BufferedImage image;
            if ( isFileDocument( url ) ) {
                image = ImageIO.read( getFile( url ) );
            } else {
                image = ImageIO.read( new URL( url ) );
            }
            size[0] = image.getWidth();
            size[1] = image.getHeight();
        } catch ( IOException e ) {
            LOG.warn( "Failed to calculate size of " + url );
            e.printStackTrace();
        }
        return size;
    }

    private boolean isFileDocument( String url ) {
        return url.startsWith( path );
    }


    private File getFile( String url ) {
        String fileName = url.replaceFirst( path, "" );
        return new File( getUploadDirectory(), fileName );
    }

    /**
     * Get the location of the uploaded files.
     *
     * @return a directory
     */
    public File getUploadDirectory() {
        try {
            return directory.getFile();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

}
